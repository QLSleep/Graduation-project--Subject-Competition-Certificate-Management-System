package cn.guet.competition_cert_system.exception;

import cn.guet.competition_cert_system.common.ResultCode;

/**
 * 权限不足异常
 */
public class ForbiddenException extends BusinessException {

    public ForbiddenException() {
        super(ResultCode.FORBIDDEN);
    }

    public ForbiddenException(String message) {
        super(ResultCode.FORBIDDEN, message);
    }
}
