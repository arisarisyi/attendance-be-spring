package com.kad.attendance.controller;

import com.kad.attendance.entities.User;
import com.kad.attendance.model.*;
import com.kad.attendance.service.CheckOutService;
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
public class CheckOutController {
    @Autowired
    private CheckOutService checkOutService;

    @PostMapping(
            path = "/check-out",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CheckOutResponse> checkOut(@RequestBody CheckOutRequest request, User user){
        CheckOutResponse data = checkOutService.checkOut(user,request);

        return WebResponse.<CheckOutResponse>builder().data(data).build();
    }

    @GetMapping(
            path = "/check-out/{checkOutId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CheckOutResponse> get(@PathVariable("checkOutId")Integer id,
                                             @Parameter(hidden = true)User user){
        CheckOutResponse data = checkOutService.get(id, user);
        return WebResponse.<CheckOutResponse>builder().data(data).build();
    }

    @GetMapping(
            path = "/api/check-out",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<CheckOutResponse>> search(@Parameter(hidden = true) User user,
                                                     @RequestParam(value = "createdAt", required = false) Date createdAt,
                                                     @RequestParam(value = "userId", required = false) UUID userId,
                                                     @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", required = false, defaultValue = "10") Integer size){
        SearchCheckOutRequest request = SearchCheckOutRequest.builder()
                .page(page)
                .size(size)
                .createdAt(createdAt)
                .userId(userId)
                .build();

        Page<CheckOutResponse> checkOutResponses = checkOutService.search(user,request);
        return WebResponse.<List<CheckOutResponse>>builder()
                .data(checkOutResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(checkOutResponses.getNumber())
                        .totalPage(checkOutResponses.getTotalPages())
                        .size(checkOutResponses.getSize())
                        .build())
                .build();
    }
}
