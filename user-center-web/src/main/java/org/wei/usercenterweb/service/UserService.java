package org.wei.usercenterweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wei.usercenterweb.domain.User;
import org.wei.usercenterweb.domain.request.GetReferralDataRequest;
import org.wei.usercenterweb.domain.request.SearchUserByTagsRequest;
import org.wei.usercenterweb.domain.request.SearchUsersRequest;
import org.wei.usercenterweb.domain.response.UserInformation;

import javax.servlet.http.HttpServletRequest;

/**
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/5
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userCount         账号
     * @param userPassword      密码
     * @param userCheckPassword 校验密码
     * @return 注册状态
     */
    boolean userRegister(String userCount, String userPassword, String userCheckPassword);

    /**
     * 用户登录
     *
     * @param userCount    账号
     * @param userPassword 密码
     * @param request      请求
     * @return 用户信息（脱密）
     */
    UserInformation userLogin(String userCount, String userPassword, HttpServletRequest request);

    /**
     * 查询用户列表
     *
     * @return 非管理员用户列表
     */
    IPage<UserInformation> searchUsers(SearchUsersRequest request);

    /**
     * 进行用户的删除，不能删除管理员用户
     *
     * @param id 用户id
     * @return 删除状态，true为删除成功，false为删除失败
     */
    boolean deleteUser(String id);

    /**
     * 根据标签检索用户
     *
     * @param request 检索条件
     * @return 检索用户数据
     */
    IPage<UserInformation> searchUserByTags(String currentUserId, SearchUserByTagsRequest request);

    /**
     * 根据用户标签获取推荐数据
     *
     * @param request 用户标签等数据
     * @return 匹配用户数据
     */
    IPage<UserInformation> getReferralData(GetReferralDataRequest request, String userId);

    /**
     * 权限校验，判断用户是否登录
     *
     * @param request 请求体
     */
    UserInformation permissionVerification(HttpServletRequest request);

    /**
     * 判断用户是否为管理员
     *
     * @param request 请求体
     */
    void isAdmin(HttpServletRequest request);

    /**
     * 实现用户数据脱敏
     *
     * @param page 分页数据
     * @return 脱敏后分页数据
     */
    IPage<UserInformation> userInformationPage(IPage<User> page);
}
