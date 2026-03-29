package cn.guet.competition_cert_system.interceptor;

import cn.guet.competition_cert_system.common.DataScope;
import cn.guet.competition_cert_system.context.AuthContext;
import cn.guet.competition_cert_system.utils.JWTUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class JWTInterceptor implements HandlerInterceptor {

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null || token.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录\"}");
            return false;
        }

        // 去掉 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证 token
        Boolean valid = jwtUtil.validateToken(token);
        if (valid == null || !valid) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"token无效或已过期\"}");
            return false;
        }

        // 解析 token，存入 AuthContext
        Claims claims = jwtUtil.extractAllClaims(token);

        AuthContext.setUserId(claims.get("userId", Long.class));
        AuthContext.setUsername(claims.get("username", String.class));
        AuthContext.setRealName(claims.get("realName", String.class));
        AuthContext.setCollegeId(claims.get("collegeId", Long.class));

        // roles
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles", List.class);
        AuthContext.setRoles(roles);

        // dataScope
        String dataScopeStr = claims.get("dataScope", String.class);
        if (dataScopeStr != null) {
            AuthContext.setDataScope(DataScope.valueOf(dataScopeStr));
        }

        // permissions
        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) claims.get("permissions", List.class);
        AuthContext.setPermissions(permissions);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求结束，清除 AuthContext，防止内存泄漏
        AuthContext.clear();
    }
}
