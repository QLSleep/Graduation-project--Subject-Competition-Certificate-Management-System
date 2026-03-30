package cn.guet.competition_cert_system.aspect;

import cn.guet.competition_cert_system.annotation.RequirePermission;
import cn.guet.competition_cert_system.common.CertAction;
import cn.guet.competition_cert_system.context.AuthContext;
import cn.guet.competition_cert_system.exception.ForbiddenException;
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

        CertAction[] requiredPermissions = annotation.value();
        List<String> requiredNames = Arrays.stream(requiredPermissions)
                .map(CertAction::name)
                .toList();

        List<String> userPermissions = AuthContext.getPermissions();
        if (userPermissions == null || userPermissions.isEmpty()) {
            throwNoPermission();
            return;
        }

        boolean hasPermission = userPermissions.stream()
                .anyMatch(requiredNames::contains);

        if (!hasPermission) {
            throwNoPermission();
        }
    }

    private void throwNoPermission() {
        throw new ForbiddenException();
    }
}
