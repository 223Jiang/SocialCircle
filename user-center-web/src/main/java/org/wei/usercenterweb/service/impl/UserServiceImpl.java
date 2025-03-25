package org.wei.usercenterweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.wei.usercenterweb.common.StatusCodeEnum;
import org.wei.usercenterweb.common.UserCache;
import org.wei.usercenterweb.domain.User;
import org.wei.usercenterweb.domain.request.GetReferralDataRequest;
import org.wei.usercenterweb.domain.request.SearchUserByTagsRequest;
import org.wei.usercenterweb.domain.request.SearchUsersRequest;
import org.wei.usercenterweb.domain.response.UserInformation;
import org.wei.usercenterweb.exception.CustomRuntimeExceptions;
import org.wei.usercenterweb.mapper.UserMapper;
import org.wei.usercenterweb.service.UserService;
import org.wei.usercenterweb.utile.BCryptEncryption;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.wei.usercenterweb.contains.UserConstants.USER_INFORMATION;
import static org.wei.usercenterweb.utile.BCryptEncryption.verifyPassword;

/**
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/5
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserCache userCache;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public boolean userRegister(String userCount, String userPassword, String userCheckPassword) {
        // 用户密码加密
        String encryptPassword = BCryptEncryption.encryptPassword(userPassword);
        User user = new User();
        user.setUserCount(userCount);
        user.setUserPassword(encryptPassword);

        // 判断账号是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserCount, userCount);
        long count = count(wrapper);

        if (count > 0) {
            throw new CustomRuntimeExceptions(StatusCodeEnum.BAD_REQUEST, "账号已存在！");
        }

        return save(user);
    }

    @Override
    public UserInformation userLogin(String userCount, String userPassword, HttpServletRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserCount, userCount);
        User user = getOne(wrapper);

        if (user == null) {
            throw new CustomRuntimeExceptions(StatusCodeEnum.BAD_REQUEST, "用户不存在！");
        }

        if (user.getUserStatus() == 1) {
            throw new CustomRuntimeExceptions(StatusCodeEnum.BAD_REQUEST, "账号已被封禁！");
        }

        if (!verifyPassword(userPassword, user.getUserPassword())) {
            throw new CustomRuntimeExceptions(StatusCodeEnum.BAD_REQUEST, "密码错误！");
        }

        // 生成脱敏后的数据
        UserInformation userInformation = new UserInformation();
        BeanUtils.copyProperties(user, userInformation);

        // 在session中存储用户信息
        request.getSession().setAttribute(USER_INFORMATION, userInformation);

        return userInformation;
    }

    @Override
    public IPage<UserInformation> searchUsers(SearchUsersRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(request.getUserName() != null, User::getUserName, request.getUserName())
                .eq(request.getIsAdmin() != null, User::getIsAdmin, request.getIsAdmin())
                .eq(request.getUserStatus() != null, User::getUserStatus, request.getUserStatus());
        if (request.getStartTime() != null) {
            wrapper.ge(User::getCreateTime, request.getStartTime());
        }
        if (request.getEndTime() != null) {
            wrapper.lt(User::getCreateTime, request.getEndTime());
        }
        IPage<User> page = page(new Page<>(request.getCurrent(), request.getPageSize()), wrapper);

        // 进行数据的脱敏操作
        return userInformationPage(page);
    }

    @Override
    public boolean deleteUser(String id) {
        // 判断用户是否是管理员，管理员不能删除
        User user = getById(id);
        if (user.getIsAdmin() == 1) {
            return false;
        }

        return removeById(id);
    }

    @Override
    public IPage<UserInformation> searchUserByTags(String currentUserId, SearchUserByTagsRequest request) {
        if (request.getTags() == null || request.getTags().isEmpty()) {
            return userInformationPage(new Page<>());
        }

        String key = String.format("user:search:by:tag_%s:page_%d:%d",
                request.getTags(), request.getCurrent(), request.getPageSize());

        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (bucket.isExists()) {
            return (IPage<UserInformation>) bucket.get();
        }

        // 构建数据库查询条件
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.ne("user_id", currentUserId)
                .eq("user_status", 0);

        // 添加标签筛选条件
        for (String tag : request.getTags()) {
            wrapper.apply("JSON_CONTAINS(tags, {0})", "\"" + tag + "\"");
        }

        // 分页查询
        Page<User> page = new Page<>(request.getCurrent(), request.getPageSize());
        Page<User> resultPage = page(page, wrapper);

        IPage<UserInformation> userInformationPage = userInformationPage(resultPage);
        bucket.set(userInformationPage, Duration.ofMinutes(30));
        return userInformationPage;
    }

    @Override
    public IPage<UserInformation> getReferralData(GetReferralDataRequest request, String currentUserId) {
        String key = generateCacheKey(currentUserId, request);
        RBucket<IPage<UserInformation>> bucket = redissonClient.getBucket(key);
        IPage<UserInformation> cachedPage = bucket.get();

        if (cachedPage != null) {
            return cachedPage;
        }

        RLock lock = redissonClient.getLock(key + "_");
        try {
            if (lock.tryLock(5, 30, TimeUnit.SECONDS)) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();

                    // 创建分页对象
                    Page<User> page = new Page<>(request.getCurrent(), request.getPageSize());

                    // 获取当前用户信息
                    User currentUser = null;
                    if (StringUtils.isNotBlank(currentUserId)) {
                        currentUser = this.getById(currentUserId);
                    }

                    // 获取所有用户
                    List<User> allUsers = userCache.getUserNotIdList(currentUserId);

                    // 计算用户相似度并排序
                    if (currentUser != null && StringUtils.isNotBlank(currentUser.getTags())) {
                        try {
                            // 解析当前用户的标签
                            List<String> currentUserTags = objectMapper.readValue(currentUser.getTags(), new TypeReference<List<String>>() {
                            });

                            // 计算每个用户与当前用户的相似度
                            allUsers = allUsers.stream()
                                    .filter(user -> StringUtils.isNotBlank(user.getTags()))
                                    .map(user -> {
                                        try {
                                            List<String> userTags = objectMapper.readValue(user.getTags(), new TypeReference<List<String>>() {
                                            });
                                            double similarity = calculateJaccardSimilarity(currentUserTags, userTags);
                                            user.setSimilarity(similarity);
                                            return user;
                                        } catch (Exception e) {
                                            user.setSimilarity(0.0);
                                            return user;
                                        }
                                    })
                                    .sorted(Comparator.comparing(User::getSimilarity).reversed())
                                    .collect(Collectors.toList());
                        } catch (Exception e) {
                            // 如果解析标签失败，按创建时间倒序排序
                            allUsers.sort(Comparator.comparing(User::getCreateTime).reversed());
                        }
                    } else {
                        // 如果没有当前用户或标签，按创建时间倒序排序
                        allUsers.sort(Comparator.comparing(user -> user.getCreateTime() == null ? "0" : user.getCreateTime(), Comparator.reverseOrder()));
                    }

                    // 设置分页结果
                    int start = (int) ((page.getCurrent() - 1) * page.getSize());
                    int end = Math.min(start + (int) page.getSize(), allUsers.size());
                    List<User> pageRecords = allUsers.subList(start, end);

                    page.setRecords(pageRecords);
                    page.setTotal(allUsers.size());

                    IPage<UserInformation> userInformationPage = userInformationPage(page);
                    bucket.set(userInformationPage, Duration.ofMinutes(5));
                    return userInformationPage;
                } finally {
                    lock.unlock();
                }
            } else {
                log.warn("未能获取锁，key: {}", key);
                return new Page<>();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("加锁中断", e);
        } catch (Exception e) {
            log.error("处理请求失败", e);
            throw new RuntimeException("系统异常", e);
        }
    }

    private double calculateJaccardSimilarity(List<String> set1, List<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return intersection.size() / (double) union.size();
    }

    @Override
    public UserInformation permissionVerification(HttpServletRequest request) {
        UserInformation user = (UserInformation) request.getSession().getAttribute(USER_INFORMATION);
        if (user == null) {
            throw new CustomRuntimeExceptions(StatusCodeEnum.UNAUTHORIZED, "用户未登录！");
        }
        // 跟新为最新的数据
        User searchUser = getById(user.getUserId());
        BeanUtils.copyProperties(searchUser, user);
        request.getSession().setAttribute(USER_INFORMATION, user);

        return user;
    }

    @Override
    public void isAdmin(HttpServletRequest request) {
        UserInformation user = permissionVerification(request);

        // 判断是否为管理员
        if (user.getIsAdmin() == 0) {
            throw new CustomRuntimeExceptions(StatusCodeEnum.METHOD_NOT_ALLOWED, "用户权限不足！");
        }
    }

    private String generateCacheKey(String userId, GetReferralDataRequest request) {
        int current = Math.max(request.getCurrent(), 1);
        int pageSize = Math.min(request.getPageSize(), 100);
        String sanitizedTags = request.getTags() == null ? "" :
                request.getTags().stream()
                        .sorted()
                        .collect(Collectors.joining(","));
        return String.format("user:referral:data:%s:page_%d:%d:tags_%s",
                userId, current, pageSize, sanitizedTags);
    }


    @Override
    public IPage<UserInformation> userInformationPage(IPage<User> page) {
        if (page == null) {
            return null;
        }

        // 进行数据的脱敏操作
        List<UserInformation> records = page.getRecords().stream().map(e -> {
            UserInformation userInformation = new UserInformation();
            BeanUtils.copyProperties(e, userInformation);
            return userInformation;
        }).collect(Collectors.toList());

        Page<UserInformation> objectPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        objectPage.setRecords(records);

        return objectPage;
    }
}




