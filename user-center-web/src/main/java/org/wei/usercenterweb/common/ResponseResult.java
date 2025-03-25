package org.wei.usercenterweb.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 *
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/5
 */
@Data
@AllArgsConstructor
public class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = 7034843670745315939L;

    private Integer code;

    private String msg;

    private T data;

    private String details;

    public ResponseResult(StatusCodeEnum codeEnum) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMessage();
    }

    public ResponseResult(StatusCodeEnum codeEnum, T data) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMessage();
        this.data = data;
    }

    public ResponseResult(StatusCodeEnum codeEnum, String details) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMessage();
        this.details = details;
    }

    public ResponseResult(StatusCodeEnum codeEnum, T data, String details) {
        this.code = codeEnum.getCode();
        this.msg = codeEnum.getMessage();
        this.data = data;
        this.details = details;
    }

    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(StatusCodeEnum.SUCCESS);
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(StatusCodeEnum.SUCCESS, data);
    }

    public static <T> ResponseResult<T> success(T data, String details) {
        return new ResponseResult<>(StatusCodeEnum.SUCCESS, data, details);
    }


    public static <T> ResponseResult<T> fail() {
        return new ResponseResult<>(StatusCodeEnum.INTERNAL_SERVER_ERROR);
    }

    public static <T> ResponseResult<T> fail(String details) {
        return new ResponseResult<>(StatusCodeEnum.INTERNAL_SERVER_ERROR, details);
    }

    @Override
    public String toString() {
        return "ResponseResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", details='" + details + '\'' +
                '}';
    }
}
