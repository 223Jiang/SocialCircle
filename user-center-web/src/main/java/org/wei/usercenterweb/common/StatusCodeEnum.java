package org.wei.usercenterweb.common;

import lombok.Getter;

/**
 * 常见编码
 *
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/5
 */

@Getter
public enum StatusCodeEnum {
    // 成功状态码
    SUCCESS(200, "操作成功"),
    // 客户端错误状态码
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源未找到"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    // 服务器错误状态码
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用");

    private final Integer code;
    private final String message;

    StatusCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}