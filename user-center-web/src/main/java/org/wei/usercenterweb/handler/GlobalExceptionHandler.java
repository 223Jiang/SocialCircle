package org.wei.usercenterweb.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.wei.usercenterweb.common.ResponseResult;
import org.wei.usercenterweb.common.StatusCodeEnum;
import org.wei.usercenterweb.exception.CustomRuntimeExceptions;

/**
 * 全局异常处理
 *
 * @author WeiWei
 * @version V5.0.0
 * @date 2025/3/5
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomRuntimeExceptions.class)
    public ResponseResult customRuntimeExceptions(CustomRuntimeExceptions e) {
        // 定义默认错误码
        StatusCodeEnum codeEnum = StatusCodeEnum.INTERNAL_SERVER_ERROR;

        // 获取错误码，如果为空则使用默认错误码
        int errorCode = e.getCode() == null ? codeEnum.getCode() : e.getCode();

        // 获取错误信息和详细信息，确保不为null
        String errorMessage = e.getMessage() != null ? e.getMessage() : codeEnum.getMessage();

        ResponseResult result = new ResponseResult(errorCode, errorMessage, null, e.getDetails());

        log.error(result.toString());
        // 返回响应结果
        return result;
    }
}
