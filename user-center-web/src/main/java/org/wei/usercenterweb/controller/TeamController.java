package org.wei.usercenterweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;
import org.wei.usercenterweb.common.ResponseResult;
import org.wei.usercenterweb.domain.request.TeamCreateRequest;
import org.wei.usercenterweb.domain.request.TeamSearchRequest;
import org.wei.usercenterweb.domain.response.TeamResponse;
import org.wei.usercenterweb.domain.response.UserInformation;
import org.wei.usercenterweb.service.TeamMemberService;
import org.wei.usercenterweb.service.TeamService;
import org.wei.usercenterweb.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * user-center-web
 *
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/21
 */
@RestController
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;
    private final TeamMemberService teamMemberService;

    public TeamController(TeamService teamService,
                          UserService userService,
                          TeamMemberService teamMemberService) {
        this.userService = userService;
        this.teamService = teamService;
        this.teamMemberService = teamMemberService;
    }

    /**
     * 创建队伍
     */
    @PostMapping("/createTeam")
    public ResponseResult<String> createTeam(
            HttpServletRequest servletRequest,
            @RequestBody TeamCreateRequest request) {
        // 判断用户是否登录
        UserInformation userInformation = userService.permissionVerification(servletRequest);

        teamService.createTeam(userInformation.getUserId(), request);
        return ResponseResult.success("队伍创建成功！");
    }

    /**
     * 用户队伍
     */
    @GetMapping("/teamsOfUsers")
    public ResponseResult<List<TeamResponse>> teamsOfUsers(
            HttpServletRequest servletRequest) {
        // 登录用户信息
        UserInformation userInformation = userService.permissionVerification(servletRequest);

        return ResponseResult.success(teamMemberService.teamsOfUsers(userInformation.getUserId()));
    }

    /**
     * 获取队伍信息
     */
    @GetMapping("/{teamId}")
    public ResponseResult<TeamResponse> getTeamById(
            HttpServletRequest servletRequest, @PathVariable String teamId) {
        // 登录用户信息
        UserInformation userInformation = userService.permissionVerification(servletRequest);

        return ResponseResult.success(teamMemberService.getTeamById(userInformation.getUserId(), teamId));
    }

    /**
     * 队伍修改
     */
    @PostMapping("/updateTeam")
    public ResponseResult<String> updateTeam(
            HttpServletRequest servletRequest,
            @RequestBody TeamCreateRequest request) {
        // 登录用户信息
        UserInformation userInformation = userService.permissionVerification(servletRequest);

        teamService.updateTeam(userInformation.getUserId(), request);
        return ResponseResult.success("队伍修改成功！");
    }

    /**
     * 队伍删除
     */
    @PostMapping("/deleteTeam/{teamId}")
    public ResponseResult<String> deleteTeam(
            HttpServletRequest servletRequest,
            @PathVariable String teamId) {
        // 登录用户信息
        UserInformation userInformation = userService.permissionVerification(servletRequest);

        teamService.deleteTeam(userInformation.getUserId(), teamId);
        return ResponseResult.success("队伍删除成功！");
    }

    /**
     * 加入队伍
     *
     * @param teamId   队伍id
     * @param password 队伍密码
     */
    @PostMapping("/joinTeam")
    public ResponseResult<String> joinTeam(
            HttpServletRequest servletRequest,
            @RequestParam("teamId") String teamId,
            @RequestParam(value = "password", required = false) String password) {
        // 登录用户信息
        UserInformation userInformation = userService.permissionVerification(servletRequest);

        teamService.joinTeam(userInformation.getUserId(), teamId, password);
        return ResponseResult.success("加入队伍成功！");
    }

    /**
     * 退出队伍
     *
     * @param teamId 队伍id
     */
    @PostMapping("/exitTeam/{teamId}")
    public ResponseResult<String> exitTeam(
            HttpServletRequest servletRequest,
            @PathVariable String teamId) {
        // 登录用户信息
        UserInformation userInformation = userService.permissionVerification(servletRequest);

        teamMemberService.exitTeam(userInformation.getUserId(), teamId);
        return ResponseResult.success("退出队伍成功！");
    }

    /**
     * 删除队伍成员
     *
     * @param teamId   队伍id
     * @param memberId 移除成员
     */
    @PostMapping("/removeMemberByLeader")
    public ResponseResult<String> removeMemberByLeader(
            HttpServletRequest servletRequest,
            @RequestParam("teamId") String teamId,
            @RequestParam("memberId") String memberId) {
        // 登录用户信息
        UserInformation userInformation = userService.permissionVerification(servletRequest);

        teamMemberService.removeMemberByLeader(userInformation.getUserId(), teamId, memberId);
        return ResponseResult.success("删除队伍成员成功！");
    }

    /**
     * 队长转移
     *
     * @param teamId   队伍id
     * @param memberId 新队长id
     */
    @PostMapping("/replaceMember")
    public ResponseResult<String> replaceMember(
            HttpServletRequest servletRequest,
            @RequestParam("teamId") String teamId,
            @RequestParam("memberId") String memberId) {
        // 登录用户信息
        UserInformation userInformation = userService.permissionVerification(servletRequest);

        teamMemberService.replaceMember(userInformation.getUserId(), teamId, memberId);
        return ResponseResult.success("队长转移成功！");
    }

    /**
     * 查询队伍数据
     *
     * @param request 队伍搜索请求
     */
    @PostMapping("/searchTeams")
    public ResponseResult<IPage<TeamResponse>> searchTeams(
            HttpServletRequest servletRequest,
            @RequestBody TeamSearchRequest request) {
        UserInformation userInformation = userService.permissionVerification(servletRequest);

        return ResponseResult.success(teamService.searchTeams(userInformation.getUserId(), request));
    }
}
