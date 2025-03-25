package org.wei.usercenterweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wei.usercenterweb.common.TeamStatusEnum;
import org.wei.usercenterweb.domain.Team;
import org.wei.usercenterweb.domain.TeamMember;
import org.wei.usercenterweb.domain.User;
import org.wei.usercenterweb.domain.request.TeamCreateRequest;
import org.wei.usercenterweb.domain.request.TeamSearchRequest;
import org.wei.usercenterweb.domain.response.TeamResponse;
import org.wei.usercenterweb.exception.CustomRuntimeExceptions;
import org.wei.usercenterweb.mapper.TeamMapper;
import org.wei.usercenterweb.mapper.TeamMemberMapper;
import org.wei.usercenterweb.service.TeamMemberService;
import org.wei.usercenterweb.service.TeamService;
import org.wei.usercenterweb.service.UserService;
import org.wei.usercenterweb.utile.BCryptEncryption;
import org.wei.usercenterweb.utile.CommonUtil;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author JiangWeiWei
 * @description 针对表【team】的数据库操作Service实现
 * @createDate 2025-03-21 21:30:00
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    private final UserService userService;
    private final TeamMemberMapper teamMemberMapper;
    private final TeamMemberService teamMemberService;
    private final RedissonClient redissonClient;

    @Lazy
    public TeamServiceImpl(UserService userService, TeamMemberMapper teamMemberMapper, TeamMemberService teamMemberService, RedissonClient redissonClient) {
        this.userService = userService;
        this.teamMemberMapper = teamMemberMapper;
        this.teamMemberService = teamMemberService;
        this.redissonClient = redissonClient;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTeam(String currentUserId, @Valid TeamCreateRequest request) {
        // 1. 校验队长是否存在
        User leader = userService.getById(currentUserId);
        if (leader == null) {
            throw new CustomRuntimeExceptions("队长用户不存在");
        }

        // 2. 校验时间格式
        LocalDateTime parsedExpireTime;
        try {
            parsedExpireTime = LocalDateTime.parse(request.getExpireTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            throw new CustomRuntimeExceptions("时间格式错误，应为 yyyy-MM-dd HH:mm:ss");
        }

        // 3. 校验队伍状态
        TeamStatusEnum.fromCode(request.getStatus());

        // 4. 构造Team实体
        Team team = new Team();
        BeanUtils.copyProperties(request, team);
        team.setNum(1);
        team.setLeaderId(currentUserId);
        team.setExpireTime(request.getExpireTime());

        // 5. 密码加密存储
        if (TeamStatusEnum.ENCRYPT.getStatus().equals(team.getStatus())) {
            team.setPassword(BCryptEncryption.encryptPassword(team.getPassword()));
        }

        // 6. 保存数据
        if (!save(team)) {
            throw new CustomRuntimeExceptions("队伍创建失败");
        }
        TeamMember teamMember = new TeamMember(currentUserId, team.getId()
                , CommonUtil.getTheCurrentTime()
                , 1, 0);
        if (!teamMemberService.save(teamMember)) {
            throw new CustomRuntimeExceptions("队长添加失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTeam(String currentUserId, @Valid TeamCreateRequest request) {

        // 1. 验证队伍存在性
        Team team = getById(request.getTeamId());
        if (team == null) {
            throw new CustomRuntimeExceptions("队伍不存在或已删除");
        }

        // 2. 权限校验（支持队长）
        permissionVerification(team, currentUserId);

        // 3. 校验过期时间
        if (StringUtils.isNotBlank(request.getExpireTime())) {
            LocalDateTime expireTime = LocalDateTime.parse(request.getExpireTime(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (expireTime.isBefore(LocalDateTime.now())) {
                throw new CustomRuntimeExceptions("过期时间不能早于当前时间");
            }
        }

        // 4. 校验最大人数
        if (request.getMaxNum() != null) {
            long currentMemberCount = teamMemberService.count(new LambdaQueryWrapper<TeamMember>()
                    .eq(TeamMember::getTeamId, team.getId()));
            if (request.getMaxNum() < currentMemberCount) {
                throw new CustomRuntimeExceptions("新最大人数不能小于当前成员数量");
            }
        }

        // 5. 数据更新
        BeanUtils.copyProperties(request, team, "id", "leader_id", "password", "create_time", "update_time");
        team.setUpdateTime(CommonUtil.getTheCurrentTime());

        // 6. 处理密码逻辑
        if (team.getStatus() == 2) {
            // 如果设置为队伍加密，需要设置密码
            if (StringUtils.isBlank(request.getPassword()) && StringUtils.isBlank(team.getPassword())) {
                throw new CustomRuntimeExceptions("加密队伍必须设置密码");
            }
            // 如果没有跟换密码，则不需要修改
            if (StringUtils.isNotBlank(request.getPassword())) {
                team.setPassword(BCryptEncryption.encryptPassword(request.getPassword()));
            }
        } else {
            // 非加密队伍清空密码
            team.setPassword(null);
        }

        updateById(team);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTeam(String currentUserId, String teamId) {
        // 1. 验证队伍存在性及状态
        Team team = getById(teamId);
        if (team == null) {
            throw new CustomRuntimeExceptions("队伍不存在");
        }
        if (team.getIsDelete() == 1) {
            throw new CustomRuntimeExceptions("队伍已解散");
        }

        // 2. 权限校验（支持队长）
        permissionVerification(team, currentUserId);

        // 3. 执行逻辑删除
        removeById(teamId);

        // 4. 清理关联关系（物理删除）
        teamMemberService.remove(new LambdaQueryWrapper<TeamMember>()
                .eq(TeamMember::getTeamId, teamId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void joinTeam(String userId, String teamId, String password) {
        RLock lock = redissonClient.getLock("lock:team:" + teamId);
        
        try {
            // 尝试加锁（最多等待 30 秒，锁持有时间 10 秒）
            if (!lock.tryLock(30, 10, TimeUnit.SECONDS)) {
                throw new CustomRuntimeExceptions("系统繁忙，请稍后重试");
            }

            // 1. 基础验证
            Team team = getById(teamId);

            validateTeamValid(team);

            // 2. 类型校验
            switch (TeamStatusEnum.fromCode(team.getStatus())) {
                case PUBLIC:
                    long currentCount = teamMemberService.countByTeamId(team.getId());
                    if (currentCount >= team.getMaxNum()) {
                        throw new CustomRuntimeExceptions("队伍已满员");
                    }
                    break;
                case PRIVATE:
                    // TODO: 加入私密队伍需要验证
                    break;
                case ENCRYPT:
                    if (StringUtils.isBlank(password)) {
                        throw new CustomRuntimeExceptions("请输入队伍密码");
                    }
                    if (!BCryptEncryption.verifyPassword(password, team.getPassword())) {
                        throw new CustomRuntimeExceptions("密码错误");
                    }
                    break;
                default:
                    throw new CustomRuntimeExceptions("未知的队伍类型");
            }
            TeamMember member = teamMemberMapper.recording(userId, teamId);
            // 3. 添加成员
            if (member != null) {
                teamMemberMapper.modifyPartyMemberStatus(userId, teamId, 0);
            } else {
                member = new TeamMember();
                member.setTeamId(team.getId());
                member.setUserId(userId);
                member.setJoinTime(CommonUtil.getTheCurrentTime());
                member.setIsLeader(0);
                teamMemberService.save(member);
            }

            // 4. 更新队伍信息
            team.setNum(team.getNum() + 1);
            updateById(team);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IPage<TeamResponse> searchTeams(String userId, TeamSearchRequest request) {
        LambdaQueryWrapper<Team> query = Wrappers.lambdaQuery();

        // 基础过滤条件
        query.ne(Team::getStatus, TeamStatusEnum.OVERDUE.getStatus())
                .ne(Team::getStatus, TeamStatusEnum.PRIVATE.getStatus());

        // 动态查询条件
        Optional.ofNullable(request.getNameKeyword())
                .ifPresent(kw -> query.like(Team::getName, kw));

        Optional.ofNullable(request.getStatus())
                .ifPresent(s -> query.eq(Team::getStatus, s));

        Optional.ofNullable(request.getMembers())
                .ifPresent(m -> query.eq(Team::getMaxNum, m));

        // 可加入条件处理
        if (Boolean.TRUE.equals(request.getJoinable())) {
            query.and(wrapper -> wrapper
                    // 使用apply方法进行字段比较
                    .apply("num < max_num")
                    .gt(Team::getExpireTime, CommonUtil.getTheCurrentTime())
            );
        }

        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getUserId, userId).select(TeamMember::getTeamId);
        List<TeamMember> teamMemberIds = teamMemberService.list(wrapper);
        if (teamMemberIds != null && !teamMemberIds.isEmpty()) {
            query.notIn(Team::getId, teamMemberIds.stream().map(TeamMember::getTeamId).collect(Collectors.toList()));
        }

        IPage<Team> page = page(new Page<>(request.getCurrent(), request.getPageSize()), query);
        // 5. 转换响应对象
        return page.convert(team -> {
            TeamResponse response = new TeamResponse();
            BeanUtils.copyProperties(team, response);

            User user = userService.getById(response.getLeaderId());
            if (user != null) {
                response.setLeaderName(user.getUserName());
            }

            // 可补充其他转换逻辑（如成员信息）
            return response;
        });
    }

    public static void validateTeamValid(Team team) {
        if (team == null) {
            throw new CustomRuntimeExceptions("队伍不存在");
        }

        if (TeamStatusEnum.OVERDUE.getStatus().equals(team.getStatus())) {
            throw new CustomRuntimeExceptions("队伍已过期");
        }
    }

    public void permissionVerification(Team team, String currentUserId) {
        boolean isLeader = team.getLeaderId().equals(currentUserId);
        if (!isLeader) {
            throw new CustomRuntimeExceptions("没有权限");
        }
    }
}




