package org.wei.usercenterweb.common;

import lombok.Getter;
import org.wei.usercenterweb.exception.CustomRuntimeExceptions;

import java.util.Arrays;

/**
 * user-center-web
 *
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/21
 */
@Getter
public enum TeamStatusEnum {
    PUBLIC(0, "公开"),
    PRIVATE(1, "私有"),
    ENCRYPT(2, "加密"),
    OVERDUE(3, "过期");

    private final Integer status;

    private final String description;

    TeamStatusEnum(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

    public static TeamStatusEnum fromCode(Integer status) {
        return Arrays.stream(values())
                .filter(e -> e.status.equals(status))
                .findFirst()
                .orElseThrow(() -> new CustomRuntimeExceptions("无效的队伍状态"));
    }
}
