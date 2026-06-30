package com.admanager.admin.controller;

import com.admanager.admin.dto.AdminProfileResponse;
import com.admanager.admin.dto.ChangePasswordRequest;
import com.admanager.admin.dto.LoginRequest;
import com.admanager.admin.dto.LoginResponse;
import com.admanager.admin.dto.LogoutRequest;
import com.admanager.admin.dto.RefreshRequest;
import com.admanager.admin.service.AdminAuthService;
import com.admanager.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/auth")
public class AdminAuthController {

    private final AdminAuthService authService;

    public AdminAuthController(AdminAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                            HttpServletRequest httpRequest) {
        return ApiResponse.success(authService.login(request, httpRequest));
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request,
                                              HttpServletRequest httpRequest) {
        return ApiResponse.success(authService.refresh(request, httpRequest));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest request,
                                    HttpServletRequest httpRequest) {
        authService.logout(request, httpRequest);
        return ApiResponse.success(null);
    }

    @GetMapping("/me")
    public ApiResponse<AdminProfileResponse> me() {
        return ApiResponse.success(authService.me());
    }

    @PostMapping("/password/change")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                            HttpServletRequest httpRequest) {
        authService.changePassword(request, httpRequest);
        return ApiResponse.success(null);
    }
}
