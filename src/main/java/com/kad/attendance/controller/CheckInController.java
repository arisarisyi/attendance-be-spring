package com.kad.attendance.controller;

import com.kad.attendance.entities.CheckIn;
import com.kad.attendance.entities.User;
import com.kad.attendance.model.CheckInRequest;
import com.kad.attendance.model.CheckInResponse;
import com.kad.attendance.model.WebResponse;
import com.kad.attendance.service.CheckInService;
import io.swagger.v3.oas.annotations.Parameter;
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
}
