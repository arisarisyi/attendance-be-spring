package com.kad.attendance.controller;

import com.kad.attendance.entities.CheckIn;
import com.kad.attendance.entities.User;
import com.kad.attendance.model.*;
import com.kad.attendance.service.CheckInService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CheckInController {
    @Autowired
    private CheckInService checkInService;

    @PostMapping(
            path = "/check-in",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CheckInResponse> checkIn(@RequestBody CheckInRequest req,User user){
        CheckInResponse data = checkInService.checkIn(user, req);

        return WebResponse.<CheckInResponse>builder().data(data).build();
    }

    @GetMapping(
            path = "/check-in/{checkInId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CheckInResponse> get(@PathVariable("checkInId")Integer checkInId,
                                            @Parameter(hidden = true) User user){

        CheckInResponse data = checkInService.get(checkInId,user);
        return WebResponse.<CheckInResponse>builder().data(data).build();
    }

    @GetMapping(
            path = "/check-in",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<CheckInResponse>> search(@Parameter(hidden = true) User user,
                                                     @RequestParam(value = "createdAt", required = false) Date createdAt,
                                                     @RequestParam(value = "userId", required = false) UUID userId,
                                                     @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        SearchCheckInRequest request = SearchCheckInRequest.builder()
                .page(page)
                .size(size)
                .createdAt(createdAt)
                .userId(userId)
                .build();

        Page<CheckInResponse> checkInResponses = checkInService.search(user, request);
        return WebResponse.<List<CheckInResponse>>builder()
                .data(checkInResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(checkInResponses.getNumber())
                        .totalPage(checkInResponses.getTotalPages())
                        .size(checkInResponses.getSize())
                        .build())
                .build();
    }
}
