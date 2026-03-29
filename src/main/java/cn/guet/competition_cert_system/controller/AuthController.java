package cn.guet.competition_cert_system.controller;

import cn.guet.competition_cert_system.context.AuthContext;
import cn.guet.competition_cert_system.service.AuthService;
import cn.guet.competition_cert_system.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginRequest request) {
        try {
            AuthService.LoginResult result = authService.login(request.getUsername(), request.getPassword());
            return Result.success(Map.of(
                    "accessToken", result.getAccessToken(),
                    "refreshToken", result.getRefreshToken()
            ));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 刷新 AccessToken
     */
    @PostMapping("/refresh")
    public Result<Map<String, String>> refresh(@RequestBody RefreshRequest request) {
        try {
            String newAccessToken = authService.refreshAccessToken(request.getRefreshToken());
            return Result.success(Map.of("accessToken", newAccessToken));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        Long userId = AuthContext.getUserId();
        authService.logout(userId);
        return Result.success();
    }

    // ==================== DTO ====================

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    public static class RefreshRequest {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }
    }
}
