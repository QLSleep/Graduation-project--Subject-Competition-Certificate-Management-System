package cn.guet.competition_cert_system.controller;

import cn.guet.competition_cert_system.context.AuthContext;
import cn.guet.competition_cert_system.dto.UserRegisterDTO;
import cn.guet.competition_cert_system.service.AuthService;
import cn.guet.competition_cert_system.service.UserService;
import cn.guet.competition_cert_system.utils.Result;
import cn.guet.competition_cert_system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

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

    /**
     * 发送注册验证码
     */
    @PostMapping("/send-register-code")
    public Result<Void> sendRegisterCode(@RequestBody SendCodeRequest request) {
        try {
            authService.sendRegisterCode(request.getEmail());
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 发送找回密码验证码
     */
    @PostMapping("/send-reset-code")
    public Result<Void> sendResetCode(@RequestBody SendResetCodeRequest request) {
        try {
            authService.sendResetCode(request.getUsername(), request.getEmail());
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(
                    request.getUsername(),
                    request.getEmail(),
                    request.getCode(),
                    request.getNewPassword()
            );
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody UserRegisterDTO dto) {
        try {
            userService.register(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser() {
        return Result.success(userService.getCurrentUser());
    }

    /**
     * 发送修改邮箱验证码到当前邮箱
     */
    @PostMapping("/send-update-email-code")
    public Result<Void> sendUpdateEmailCode() {
        try {
            authService.sendUpdateEmailCode();
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 校验验证码并更新邮箱
     */
    @PostMapping("/update-email")
    public Result<Void> updateEmail(@RequestBody UpdateEmailRequest request) {
        try {
            authService.updateEmail(request.getCode(), request.getNewEmail());
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码（需登录）
     */
    @PutMapping("/change-password")
    public Result<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            authService.changePassword(request.getOldPassword(), request.getNewPassword(), request.getConfirmPassword());
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // ==================== DTO ====================

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class RefreshRequest {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    public static class SendCodeRequest {
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class SendResetCodeRequest {
        private String username;
        private String email;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class ResetPasswordRequest {
        private String username;
        private String email;
        private String code;
        private String newPassword;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    public static class UpdateEmailRequest {
        private String code;
        private String newEmail;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getNewEmail() {
            return newEmail;
        }

        public void setNewEmail(String newEmail) {
            this.newEmail = newEmail;
        }
    }

    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
        private String confirmPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
}
