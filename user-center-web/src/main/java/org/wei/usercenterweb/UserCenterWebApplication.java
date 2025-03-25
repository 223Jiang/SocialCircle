package org.wei.usercenterweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@MapperScan("org.wei.usercenterweb.mapper")
@EnableRedisHttpSession
@EnableScheduling
public class UserCenterWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserCenterWebApplication.class, args);
    }

}
