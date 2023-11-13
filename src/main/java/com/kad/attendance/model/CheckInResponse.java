package com.kad.attendance.model;

import com.kad.attendance.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckInResponse {
    private Long id;
    private Double latitude;
    private Double longitude;
    private String date;
    private String month;
    private String year;
    private String time;
    private Date createdAt;
    private UserResponse user;
}
