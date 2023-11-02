package com.kad.attendance.controller;

import com.kad.attendance.entities.User;
import com.kad.attendance.model.RegisterUserRequest;
import com.kad.attendance.model.UpdateUserRequest;
import com.kad.attendance.model.UserResponse;
import com.kad.attendance.model.WebResponse;
import com.kad.attendance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(
            path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody RegisterUserRequest request){
        userService.register(request);
        return WebResponse.<String>builder().data("OK").build();
    }

        @PatchMapping(
                path = "/update",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE
        )
        public WebResponse<UserResponse> update (
                User user,
                @RequestBody UpdateUserRequest request,
                @RequestHeader("Authorization") String authHeader){
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String jwtToken = authHeader.replace("Bearer ", "");
                UserResponse userResponse = userService.update(jwtToken, user, request);
                return WebResponse.<UserResponse>builder().data(userResponse).build();
            }
            return WebResponse.<UserResponse>builder().build();
        }

    @GetMapping(
            path = "/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> getUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.replace("Bearer ", "");
            UserResponse userResponse = userService.getData(jwtToken);
            return WebResponse.<UserResponse>builder().data(userResponse).build();
        }
        return WebResponse.<UserResponse>builder().build();
    }
    }

