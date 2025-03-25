package org.wei.usercenterweb.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wei.usercenterweb.common.UserCache;
import org.wei.usercenterweb.domain.User;
import org.wei.usercenterweb.domain.response.UserInformation;
import org.wei.usercenterweb.service.UserService;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * user-center-web
 *
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/20
 */
@Slf4j
@Component
public class ReferralDataScheduler {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserService userService;

    @Resource
    private UserCache userCache;

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.MINUTES)
    public void refreshUserCache() {
        log.info("开始刷新用户缓存");
        try {
            List<User> users = userService.list(new LambdaQueryWrapper<User>()
                    .select(User::getUserId, User::getTags, User::getUserName, User::getUserDescription, User::getImageUrl)
                    .eq(User::getUserStatus, 0));

            userCache.refreshCache(users);
            log.info("用户缓存刷新完成，共加载{}条数据", users.size());
        } catch (Exception e) {
            log.error("缓存刷新失败", e);
        }
    }

    //    @Scheduled(cron = "* */30 * * * ?")
    public void runScheduleFixedRate() {
        String key = String.format("user:referral:data:%s:page_%d:%d:tags_%s",
                null, 1, 10, null);

        RLock lock = redissonClient.getLock(key + "_");
        try {
            if (lock.tryLock(5, -1, TimeUnit.SECONDS)) {
                RBucket<Object> bucket = redissonClient.getBucket(key);

                Page<User> page = userService.page(new Page<>(1, 10));
                Thread.sleep(40000);

                // 进行数据的脱敏操作
                IPage<UserInformation> objectPage = userService.userInformationPage(page);
                bucket.set(objectPage, Duration.ofMinutes(5));
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("加锁中断", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
