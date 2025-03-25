package org.wei.usercenterweb.exception;

import lombok.Getter;
import org.wei.usercenterweb.common.StatusCodeEnum;

/**
 * 自定义运行时异常
 *
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/5
 */
@Getter
public class CustomRuntimeExceptions extends RuntimeException{

    private static final long serialVersionUID = -7034843670745766939L;

    private Integer code;

    private String details;

    public CustomRuntimeExceptions(Exception e) {
        super(e);
    }

    public CustomRuntimeExceptions(String message) {
        super(message);
    }

    public CustomRuntimeExceptions(int code, String message) {
        super(message);
        this.code = code;
    }

    public CustomRuntimeExceptions(int code, String message, String details) {
        super(message);
        this.code = code;
        this.details = details;
    }

    public CustomRuntimeExceptions(StatusCodeEnum codeEnum, String details) {
        super(codeEnum.getMessage());
        this.code = codeEnum.getCode();
        this.details = details;
    }
}
