package com.awsome.shop.auth.facade.http.controller;

import com.awsome.shop.auth.application.api.dto.user.UserDTO;
import com.awsome.shop.auth.application.api.dto.user.request.GetUserRequest;
import com.awsome.shop.auth.application.api.dto.user.request.ListUserRequest;
import com.awsome.shop.auth.application.api.dto.user.request.UpdateUserRequest;
import com.awsome.shop.auth.application.api.service.user.UserApplicationService;
import com.awsome.shop.auth.common.dto.PageResult;
import com.awsome.shop.auth.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户 Controller
 */
@Tag(name = "User", description = "用户管理接口")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;

    @Operation(summary = "获取当前用户信息")
    @PostMapping("/private/user/current")
    public Result<UserDTO> getCurrentUser(@RequestHeader("X-Operator-Id") Long operatorId) {
        return Result.success(userApplicationService.getCurrentUser(operatorId));
    }

    @Operation(summary = "获取用户详情（管理员）")
    @PostMapping("/admin/user/get")
    public Result<UserDTO> getUser(@RequestBody @Valid GetUserRequest request) {
        return Result.success(userApplicationService.getUser(request));
    }

    @Operation(summary = "分页查询用户列表（管理员）")
    @PostMapping("/admin/user/list")
    public Result<PageResult<UserDTO>> listUsers(@RequestBody @Valid ListUserRequest request) {
        return Result.success(userApplicationService.listUsers(request));
    }

    @Operation(summary = "更新用户信息（管理员）")
    @PostMapping("/admin/user/update")
    public Result<UserDTO> updateUser(@RequestBody @Valid UpdateUserRequest request) {
        return Result.success(userApplicationService.updateUser(request));
    }
}
