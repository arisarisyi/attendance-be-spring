package com.kad.attendance.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckInRequest {

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;
}
