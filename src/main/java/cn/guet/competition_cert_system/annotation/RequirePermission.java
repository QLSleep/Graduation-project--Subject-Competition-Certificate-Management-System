package cn.guet.competition_cert_system.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记方法需要的权限
 * 使用：@RequirePermission("REVIEW")
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

    /**
     * 需要的功能权限，多个用逗号分隔
     * 如：@RequirePermission("REVIEW")
     * 如：@RequirePermission({"APPROVE", "REJECT"})
     */
    String[] value();
}
