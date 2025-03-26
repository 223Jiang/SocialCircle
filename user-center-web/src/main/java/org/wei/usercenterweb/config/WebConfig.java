package org.wei.usercenterweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许所有路径的跨域请求
        registry.addMapping("/**")
                // 允许所有来源（生产环境建议指定具体的域名）
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:7890", "https://um.creativityhq.club")
                // 允许的 HTTP 方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许所有请求头
                .allowedHeaders("*")
                // 是否允许发送 Cookie，默认为 false
                .allowCredentials(true)
                // 预检请求的缓存时间（单位：秒）
                .maxAge(3600);
    }
}