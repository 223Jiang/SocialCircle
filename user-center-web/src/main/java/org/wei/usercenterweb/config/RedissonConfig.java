package org.wei.usercenterweb.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * user-center-web
 *
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/19
 */
@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                // 最小空闲连接数
                .setConnectionMinimumIdleSize(10)
                // 最大连接池大小
                .setConnectionPoolSize(64)
                // 连接超时时间（毫秒）
                .setTimeout(3000)
                // 重试次数
                .setRetryAttempts(3)
                // 重试间隔（毫秒）
                .setRetryInterval(1500)
                // 选择数据库
                .setDatabase(3);

        // 连接池优化配置
        config.useSingleServer()
                // 空闲连接超时
                .setIdleConnectionTimeout(10000)
                // 连接建立超时
                .setConnectTimeout(10000)
                // 订阅连接池大小
                .setSubscriptionConnectionPoolSize(50);

        // 全局配置（可选）
        // 序列化方式
        config.setCodec(new JsonJacksonCodec())
                // 线程池大小
                .setThreads(16)
                // Netty线程池
                .setNettyThreads(32)
                // 分布式锁看门狗超时
                .setLockWatchdogTimeout(30000);

        return Redisson.create(config);
    }
}
