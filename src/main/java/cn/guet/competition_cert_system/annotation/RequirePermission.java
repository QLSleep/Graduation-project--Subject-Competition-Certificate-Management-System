package cn.guet.competition_cert_system.annotation;

import cn.guet.competition_cert_system.common.CertAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记方法需要的操作权限
 * 使用：@RequirePermission(CertAction.AUDIT)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

    /**
     * 需要的功能权限，支持多个
     * 如：@RequirePermission(CertAction.AUDIT)
     * 如：@RequirePermission({CertAction.APPROVE, CertAction.REJECT})
     */
    CertAction[] value();
}
