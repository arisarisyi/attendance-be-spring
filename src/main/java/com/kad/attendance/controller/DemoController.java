package com.kad.attendance.controller;

import com.kad.attendance.model.WebResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo-controller")
public class DemoController {
    @GetMapping
    public WebResponse<String> sayHello() {
        return WebResponse.<String>builder().data("OK").build();
    }
}
