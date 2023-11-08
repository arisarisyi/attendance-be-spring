package com.kad.attendance.controller;

import com.kad.attendance.entities.CheckIn;
import com.kad.attendance.model.CheckInRequest;
import com.kad.attendance.model.CheckInResponse;
import com.kad.attendance.model.WebResponse;
import com.kad.attendance.service.CheckInService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
    public WebResponse<CheckInResponse> checkIn(@RequestHeader("Authorization") String authHeader, @RequestBody CheckInRequest req){
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.replace("Bearer ", "");
            CheckInResponse data = checkInService.checkIn(jwtToken, req);

            return WebResponse.<CheckInResponse>builder().data(data).build();
        }
        return null;
    }

    @GetMapping(
            path = "/check-in/{checkInId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CheckInResponse> get(@RequestHeader("Authorization") String authHeader,
                                            @PathVariable("checkInId")int checkInId){
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.replace("Bearer ", "");
            CheckInResponse data = checkInService.get(jwtToken, checkInId);
            return WebResponse.<CheckInResponse>builder().data(data).build();
        }
        return null;
    }
}
