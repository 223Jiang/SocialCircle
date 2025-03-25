package org.wei.usercenterweb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.wei.usercenterweb.domain.TeamMember;
import org.wei.usercenterweb.domain.response.TeamResponse;

import java.util.List;

/**
 * @author JiangWeiWei
 * @description 针对表【team_member】的数据库操作Service
 * @createDate 2025-03-21 21:31:17
 */
public interface TeamMemberService extends IService<TeamMember> {

    /**
     * 根据用户查询队伍列表
     *
     * @param userId 用户id
     */
    List<TeamResponse> teamsOfUsers(String userId);

    /**
     * 队伍人数
     *
     * @param teamId 队伍id
     * @return 队伍人数
     */
    long countByTeamId(String teamId);

    /**
     * 获取当前用户在队伍中的信息
     *
     * @param userId 用户id
     * @param teamId 队伍id
     * @return 队伍成员
     */
    TeamMember getActiveMember(String userId, String teamId);

    /**
     * 退出队伍
     *
     * @param userId 用户id
     * @param teamId 队伍id
     */
    void exitTeam(String userId, String teamId);

    /**
     * 删除队伍成员
     *
     * @param operatorId 操作人
     * @param teamId     队伍id
     * @param memberId   成员id
     */
    void removeMemberByLeader(String operatorId, String teamId, String memberId);

    /**
     * 队长转移
     *
     * @param operatorId 操作人
     * @param teamId     队伍id
     * @param memberId   新队长id
     */
    void replaceMember(String operatorId, String teamId, String memberId);

    /**
     * 根据队伍id，获取队伍信息
     *
     * @param teamId 队伍id
     * @return 队伍信息
     */
    TeamResponse getTeamById(String userId, String teamId);
}
