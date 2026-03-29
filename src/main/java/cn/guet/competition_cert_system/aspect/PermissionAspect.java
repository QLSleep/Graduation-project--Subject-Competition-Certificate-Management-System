package cn.guet.competition_cert_system.aspect;

import cn.guet.competition_cert_system.annotation.RequirePermission;
import cn.guet.competition_cert_system.context.AuthContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class PermissionAspect {

    @Pointcut("@annotation(cn.guet.competition_cert_system.annotation.RequirePermission)")
    public void permissionPointcut() {
    }

    @Before("permissionPointcut()")
    public void checkPermission(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission annotation = method.getAnnotation(RequirePermission.class);
        if (annotation == null) {
            return;
        }

        String[] requiredPermissions = annotation.value();
        List<String> requiredList = Arrays.asList(requiredPermissions);

        List<String> userPermissions = AuthContext.getPermissions();
        if (userPermissions == null || userPermissions.isEmpty()) {
            throwNoPermission();
            return;
        }

        boolean hasPermission = userPermissions.stream()
                .anyMatch(requiredList::contains);

        if (!hasPermission) {
            throwNoPermission();
        }
    }

    private void throwNoPermission() {
        throw new RuntimeException("无权限操作");
    }
}
