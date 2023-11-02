package com.kad.attendance.controller;

import com.kad.attendance.model.WebResponse;
import com.kad.attendance.service.CheckInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckInController {
    @Autowired
    private CheckInService checkInService;

    @GetMapping("/secure-endpoint")
    public WebResponse<String> checkIn(@RequestHeader("Authorization") String authHeader){
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.replace("Bearer ", "");
            checkInService.checkIn(jwtToken);
            return WebResponse.<String>builder().data("OK").build();
        }
        return WebResponse.<String>builder().data("OK").build();
    }
}
