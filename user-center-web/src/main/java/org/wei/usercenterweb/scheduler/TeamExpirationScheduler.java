package org.wei.usercenterweb.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.wei.usercenterweb.domain.Team;
import org.wei.usercenterweb.domain.TeamMember;
import org.wei.usercenterweb.service.TeamMemberService;
import org.wei.usercenterweb.service.TeamService;
import org.wei.usercenterweb.utile.CommonUtil;

import java.util.List;

/**
 * 定期处理过期的队伍
 *
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/22
 */
@Slf4j
@Component
public class TeamExpirationScheduler {
    private final TeamService teamService;

    private final TeamMemberService teamMemberService;

    public TeamExpirationScheduler(TeamService teamService, TeamMemberService teamMemberService) {
        this.teamService = teamService;
        this.teamMemberService = teamMemberService;
    }

    /**
     * 每分钟执行一次过期检查
     */
    @Scheduled(cron = "0 * * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void checkExpiredTeams() {
        log.info("开始执行队伍过期检查任务...");

        // 1. 查询所有未过期且未删除的队伍
        List<Team> activeTeams = teamService.list(new LambdaQueryWrapper<Team>()
                .lt(Team::getExpireTime, CommonUtil.getTheCurrentTime())
                .and(qw -> qw.eq(Team::getIsDelete, 0).or().ne(Team::getStatus, 3)));

        if (CollectionUtils.isEmpty(activeTeams)) {
            log.info("没有需要处理的过期队伍");
            return;
        }

        // 2. 处理每个过期队伍
        activeTeams.forEach(team -> {
            try {
                processExpiredTeam(team);
                log.info("队伍[{}]已处理过期状态", team.getId());
            } catch (Exception e) {
                log.error("处理队伍[{}]过期时发生异常", team.getId(), e);
            }
        });
    }

    public void processExpiredTeam(Team team) {
        // 1. 更新队伍状态为过期
        team.setStatus(3);
        team.setIsDelete(1);
        teamService.updateById(team);

        // 2. 移除所有成员关系（可选：保留成员记录）
        teamMemberService.remove(new LambdaQueryWrapper<TeamMember>()
                .eq(TeamMember::getTeamId, team.getId()));
    }
}
