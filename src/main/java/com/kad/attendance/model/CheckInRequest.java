package com.kad.attendance.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckInRequest {

    @NotBlank
    private Double latitude;

    @NotBlank
    private Double longitude;
}
