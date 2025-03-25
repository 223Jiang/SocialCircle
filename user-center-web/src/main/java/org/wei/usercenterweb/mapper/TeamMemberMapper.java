package org.wei.usercenterweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.wei.usercenterweb.domain.TeamMember;

/**
 * @author JiangWeiWei
 * @description 针对表【team_member】的数据库操作Mapper
 * @createDate 2025-03-21 21:31:17
 * @Entity org.wei.usercenterweb.domain.TeamMember
 */
public interface TeamMemberMapper extends BaseMapper<TeamMember> {
    /**
     * 查询队伍成员（可以查询到删除的数据）
     *
     * @param userId 用户id
     * @param teamId 队伍id
     * @return 队伍成员信息
     */
    TeamMember recording(@Param("userId") String userId, @Param("teamId") String teamId);

    /**
     * 修改队伍成员状态
     *
     * @param userId 用户id
     * @param teamId 队伍id
     * @param status 成员状态
     */
    void modifyPartyMemberStatus(@Param("userId") String userId, @Param("teamId") String teamId, @Param("status") int status);
}




