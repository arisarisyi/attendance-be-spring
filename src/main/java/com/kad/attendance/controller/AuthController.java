package com.kad.attendance.controller;

import com.kad.attendance.model.LoginUserRequest;
import com.kad.attendance.model.TokenResponse;
import com.kad.attendance.model.WebResponse;
import com.kad.attendance.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return WebResponse.<TokenResponse>builder().data(tokenResponse).build();
    }

    @PostMapping(
            path = "/refresh-token",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<?> refreshToken(HttpServletRequest request,@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.replace("Bearer ", "");
            TokenResponse authResponse = authService.refreshToken(request);
            return WebResponse.builder().data(authResponse).build();
        }
        return null;
    }

    @DeleteMapping(
            path = "/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String>logout ( @RequestHeader("Authorization") String authHeader){
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.replace("Bearer ", "");
            authService.logout(jwtToken);
            return WebResponse.<String>builder().data("OK").build();
        }
        return null;
    }
}
