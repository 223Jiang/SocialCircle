package org.wei.usercenterweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wei.usercenterweb.domain.Team;
import org.wei.usercenterweb.domain.TeamMember;
import org.wei.usercenterweb.domain.User;
import org.wei.usercenterweb.domain.response.TeamResponse;
import org.wei.usercenterweb.domain.response.UserInformation;
import org.wei.usercenterweb.exception.CustomRuntimeExceptions;
import org.wei.usercenterweb.mapper.TeamMemberMapper;
import org.wei.usercenterweb.service.TeamMemberService;
import org.wei.usercenterweb.service.TeamService;
import org.wei.usercenterweb.service.UserService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.wei.usercenterweb.service.impl.TeamServiceImpl.validateTeamValid;

/**
 * @author JiangWeiWei
 * @description 针对表【team_member】的数据库操作Service实现
 * @createDate 2025-03-21 21:31:17
 */
@Service
public class TeamMemberServiceImpl extends ServiceImpl<TeamMemberMapper, TeamMember>
        implements TeamMemberService {

    private final TeamService teamService;
    private final UserService userService;

    public TeamMemberServiceImpl(TeamService teamService, UserService userService) {
        this.teamService = teamService;
        this.userService = userService;
    }

    @Override
    public List<TeamResponse> teamsOfUsers(String userId) {
        // 1. 获取用户所属的所有团队ID
        LambdaQueryWrapper<TeamMember> teamIdWrapper = new LambdaQueryWrapper<>();
        teamIdWrapper.eq(TeamMember::getUserId, userId)
                .select(TeamMember::getTeamId);
        List<String> teamIds = list(teamIdWrapper).stream()
                .map(TeamMember::getTeamId)
                .collect(Collectors.toList());

        if (teamIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 查询所有相关团队信息
        List<Team> teams = teamService.listByIds(teamIds);

        // 3. 批量查询所有团队成员关系
        LambdaQueryWrapper<TeamMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.in(TeamMember::getTeamId, teamIds)
                .select(TeamMember::getTeamId, TeamMember::getUserId);
        List<TeamMember> teamMembers = list(memberWrapper);

        // 按团队ID分组成员ID
        Map<String, List<String>> teamMemberMap = teamMembers.stream()
                .collect(Collectors.groupingBy(
                        TeamMember::getTeamId,
                        Collectors.mapping(TeamMember::getUserId, Collectors.toList())
                ));

        // 4. 收集所有需要查询的用户ID
        Set<String> allUserIds = teamMembers.stream()
                .map(TeamMember::getUserId)
                .collect(Collectors.toSet());

        // 5. 批量查询用户信息并转换
        List<User> users = userService.listByIds(new ArrayList<>(allUserIds));
        Map<String, UserInformation> userMap = users.stream()
                .map(user -> {
                    UserInformation info = new UserInformation();
                    BeanUtils.copyProperties(user, info);
                    return info;
                })
                .collect(Collectors.toMap(
                        UserInformation::getUserId,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        // 6. 构建响应对象
        return teams.stream().map(team -> {
            TeamResponse response = new TeamResponse();
            BeanUtils.copyProperties(team, response);

            List<String> memberIds = teamMemberMap.getOrDefault(team.getId(), Collections.emptyList());
            List<UserInformation> members = memberIds.stream()
                    .map(userMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            response.setUsers(members);
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public long countByTeamId(String teamId) {
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId);

        return count(wrapper);
    }

    @Override
    public TeamMember getActiveMember(String userId, String teamId) {
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getUserId, userId);
        wrapper.eq(TeamMember::getTeamId, teamId);

        TeamMember member = getOne(wrapper, false);
        if (member == null) {
            throw new CustomRuntimeExceptions("成员不在队伍中");
        }
        return member;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exitTeam(String userId, String teamId) {
        // 1. 验证队伍有效性
        Team team = teamService.getById(teamId);
        validateTeamValid(team);

        // 2. 验证成员存在性
        TeamMember member = getActiveMember(userId, teamId);

        // 3. 禁止队长自行退出（根据业务规则调整）
        if (userId.equals(team.getLeaderId())) {
            throw new CustomRuntimeExceptions("队长不能直接退出队伍，请先转让队长权限");
        }

        // 4. 执行退出操作
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getUserId, userId).eq(TeamMember::getTeamId, teamId);
        remove(wrapper);

        team.setNum(team.getNum() - 1);
        teamService.updateById(team);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMemberByLeader(String operatorId, String teamId, String memberId) {
        // 1. 验证队伍有效性
        Team team = teamService.getById(teamId);
        validateTeamValid(team);

        // 2. 验证操作权限
        checkLeaderPermission(team, operatorId);

        // 3. 验证被操作成员存在性
        TeamMember memberToRemove = getActiveMember(memberId, teamId);

        // 4. 禁止移除队长自己
        if (memberToRemove.getUserId().equals(team.getLeaderId())) {
            throw new CustomRuntimeExceptions("不能移除队长自己");
        }

        // 5. 执行移除操作
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getUserId, memberId).eq(TeamMember::getTeamId, teamId);
        remove(wrapper);

        team.setNum(team.getNum() - 1);
        teamService.updateById(team);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceMember(String operatorId, String teamId, String memberId) {
        // 1. 验证队伍有效性
        Team team = teamService.getById(teamId);
        validateTeamValid(team);

        // 2. 验证操作权限
        checkLeaderPermission(team, operatorId);

        // 3. 验证原成员存在性
        TeamMember newMember = getActiveMember(memberId, team.getId());
        // 防止重复转让
        if (memberId.equals(team.getLeaderId())) {
            throw new CustomRuntimeExceptions("该用户已经是队长");
        }

        // 4. 执行替换操作
        // 4.1. 更新队伍表的队长信息
        team.setLeaderId(memberId);
        teamService.updateById(team);

        // 4.2. 更新成员表的队长标识（使用LambdaUpdateWrapper更安全）
        // 移除旧队长标识
        update(new LambdaUpdateWrapper<TeamMember>()
                .eq(TeamMember::getTeamId, team.getId())
                .eq(TeamMember::getUserId, operatorId)
                .set(TeamMember::getIsLeader, 0));

        // 设置新队长标识
        update(new LambdaUpdateWrapper<TeamMember>()
                .eq(TeamMember::getTeamId, team.getId())
                .eq(TeamMember::getUserId, memberId)
                .set(TeamMember::getIsLeader, 1));
    }

    @Override
    public TeamResponse getTeamById(String userId, String teamId) {
        // 1. 获取团队信息并验证有效性
        Team team = teamService.getById(teamId);
        validateTeamValid(team);

        // 2. 查询团队成员ID列表
        LambdaQueryWrapper<TeamMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(TeamMember::getTeamId, teamId)
                .select(TeamMember::getUserId);
        List<TeamMember> teamMembers = list(memberWrapper);
        List<String> userIds = teamMembers.stream()
                .map(TeamMember::getUserId)
                .collect(Collectors.toList());
        if (!userIds.contains(userId)) {
            throw new CustomRuntimeExceptions("用户不在团队中");
        }

        // 3. 批量查询用户信息并转换
        List<User> users = userService.listByIds(userIds);
        Map<String, UserInformation> userMap = users.stream()
                .map(user -> {
                    UserInformation info = new UserInformation();
                    BeanUtils.copyProperties(user, info);
                    return info;
                })
                .collect(Collectors.toMap(
                        UserInformation::getUserId,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        // 4. 构建响应对象
        TeamResponse response = new TeamResponse();
        BeanUtils.copyProperties(team, response);

        // 填充成员信息，过滤掉可能不存在的用户（如用户被删除但成员关系未清理）
        List<UserInformation> members = userIds.stream()
                .map(userMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        response.setUsers(members);

        return response;
    }

    /**
     * 判断用户是否队长
     *
     * @param team       队伍
     * @param operatorId 队长id
     */
    public static void checkLeaderPermission(Team team, String operatorId) {
        boolean isLeader = team.getLeaderId().equals(operatorId);
        if (!isLeader) {
            throw new CustomRuntimeExceptions("没有操作权限");
        }
    }
}




