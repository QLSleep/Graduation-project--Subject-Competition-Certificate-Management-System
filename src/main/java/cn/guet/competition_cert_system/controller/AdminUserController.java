package cn.guet.competition_cert_system.controller;

import cn.guet.competition_cert_system.annotation.RequirePermission;
import cn.guet.competition_cert_system.common.CertAction;
import cn.guet.competition_cert_system.dto.UserCreateDTO;
import cn.guet.competition_cert_system.service.UserService;
import cn.guet.competition_cert_system.utils.Result;
import cn.guet.competition_cert_system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    /**
     * 管理员创建用户
     * 超管可创建所有类型，院管只能创建学生/教师
     */
    @PostMapping
    @RequirePermission(CertAction.CREATE)
    public Result<Void> createUser(@RequestBody UserCreateDTO dto) {
        try {
            userService.createUser(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查看指定用户信息
     */
    @GetMapping("/{userId}")
    @RequirePermission(CertAction.EDIT)
    public Result<UserVO> getUserById(@PathVariable Long userId) {
        try {
            return Result.success(userService.getUserById(userId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
