package cn.guet.competition_cert_system.controller;

import cn.guet.competition_cert_system.annotation.RequirePermission;
import cn.guet.competition_cert_system.common.CertAction;
import cn.guet.competition_cert_system.dto.EnableUserDTO;
import cn.guet.competition_cert_system.dto.UserCreateDTO;
import cn.guet.competition_cert_system.dto.UpdateUserDTO;
import cn.guet.competition_cert_system.service.UserService;
import cn.guet.competition_cert_system.utils.Result;
import cn.guet.competition_cert_system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 列表查看用户
     * 超管：查询所有非超管用户
     * 院管：查询本院师生
     */
    @GetMapping
    @RequirePermission(CertAction.EDIT)
    public Result<List<UserVO>> listUsers() {
        try {
            return Result.success(userService.listUsers());
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改用户信息
     * 超管：可修改所有字段（除 id、createdAt、updatedAt）
     * 院管：可修改本院师生 username、realName、phone、studentId、teacherId（不能改学院）
     * 学生/教师：只能修改自己的 username、realName、phone（不能改学院）
     */
    @PutMapping("/{userId}")
    @RequirePermission(CertAction.EDIT)
    public Result<Void> updateUser(@PathVariable Long userId, @RequestBody UpdateUserDTO dto) {
        try {
            userService.updateUser(userId, dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 启用/禁用用户
     * 超管：可禁用任意非超管用户
     * 院管：可禁用本院师生（不能禁用自己，不能禁用其他院管）
     */
    @PatchMapping("/{userId}/enabled")
    @RequirePermission(CertAction.EDIT)
    public Result<Void> setUserEnabled(@PathVariable Long userId, @RequestBody EnableUserDTO dto) {
        try {
            userService.setUserEnabled(userId, dto.getEnabled());
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
