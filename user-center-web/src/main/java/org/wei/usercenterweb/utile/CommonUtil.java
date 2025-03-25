package org.wei.usercenterweb.utile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * user-center-web
 *
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/22
 */
public class CommonUtil {

    /**
     * 获取当前时间 yyyy-MM-dd HH:mm:ss
     *
     * @return 当前时间
     */
    public static String getTheCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
