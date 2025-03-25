package org.wei.usercenterweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.wei.usercenterweb.domain.Team;
import org.wei.usercenterweb.domain.request.TeamCreateRequest;
import org.wei.usercenterweb.domain.request.TeamSearchRequest;
import org.wei.usercenterweb.domain.response.TeamResponse;

/**
 * @author JiangWeiWei
 * @description 针对表【team】的数据库操作Service
 * @createDate 2025-03-21 21:30:00
 */
public interface TeamService extends IService<Team> {

    /**
     * 队伍创建
     *
     * @param request 队伍基本信息
     */
    void createTeam(String currentUserId, TeamCreateRequest request);

    /**
     * 队伍修改
     */
    void updateTeam(String currentUserId, TeamCreateRequest request);

    /**
     * 队伍修改
     */
    void deleteTeam(String currentUserId, String teamId);

    /**
     * 加入队伍
     *
     * @param userId   用户id
     * @param teamId   队伍id
     * @param password 队伍密码
     */
    void joinTeam(String userId, String teamId, String password);


    /**
     * 查询队伍数据
     *
     * @param request 队伍搜索请求
     */
    IPage<TeamResponse> searchTeams(String userId, TeamSearchRequest request);
}
