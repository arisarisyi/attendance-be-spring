package com.kad.attendance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckOutResponse {
    private Long id;
    private Double latitude;
    private Double longitude;
    private UserResponse user;
}
