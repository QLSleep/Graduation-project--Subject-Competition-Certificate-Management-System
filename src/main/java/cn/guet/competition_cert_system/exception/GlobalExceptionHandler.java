package cn.guet.competition_cert_system.exception;

import cn.guet.competition_cert_system.common.ResultCode;
import cn.guet.competition_cert_system.utils.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        String message = e.getMessage();
        if (message != null && message.contains("无权限")) {
            return Result.error(ResultCode.FORBIDDEN);
        }
        if (message != null && message.contains("Token")) {
            return Result.error(ResultCode.TOKEN_EXPIRED);
        }
        return Result.error(ResultCode.BUSINESS_ERROR.getCode(), message != null ? message : "业务异常");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.error(ResultCode.SYSTEM_ERROR);
    }
}
