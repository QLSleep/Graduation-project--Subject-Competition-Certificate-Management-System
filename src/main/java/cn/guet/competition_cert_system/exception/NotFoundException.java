package cn.guet.competition_cert_system.exception;

import cn.guet.competition_cert_system.common.ResultCode;

/**
 * 资源不存在异常
 */
public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super(ResultCode.NOT_FOUND, message);
    }
}
