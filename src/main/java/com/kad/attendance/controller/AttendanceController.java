package com.kad.attendance.controller;

import com.kad.attendance.entities.User;
import com.kad.attendance.model.*;
import com.kad.attendance.service.AttendanceService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping(
            path = "/attendance",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<AttendanceResponse>> search(@Parameter(hidden = true) User user,
                                                        @RequestParam(value = "createdAt", required = false) Date createdAt,
                                                        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                        @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        SearchAttendanceRequest request = SearchAttendanceRequest.builder()
                .page(page)
                .size(size)
                .createdAt(createdAt)
                .build();

        Page<AttendanceResponse> attendanceResponses = attendanceService.search(user,request);
        return WebResponse.<List<AttendanceResponse>>builder()
                .data(attendanceResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(attendanceResponses.getNumber())
                        .totalPage(attendanceResponses.getTotalPages())
                        .size(attendanceResponses.getSize())
                        .build())
                .build();
    }
}
