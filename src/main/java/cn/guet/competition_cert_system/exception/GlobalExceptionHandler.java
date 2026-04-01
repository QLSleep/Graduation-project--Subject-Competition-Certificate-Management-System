package cn.guet.competition_cert_system.exception;

import cn.guet.competition_cert_system.common.ResultCode;
import cn.guet.competition_cert_system.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getResultCode().getCode(), e.getMessage());
        return Result.error(e.getResultCode().getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        // 开发环境：返回异常详情帮助调试
        String message = "系统异常: " + e.getClass().getSimpleName() + " - " + e.getMessage();
        return Result.error(ResultCode.SYSTEM_ERROR.getCode(), message);
    }
}
