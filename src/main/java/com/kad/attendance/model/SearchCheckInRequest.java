package com.kad.attendance.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchCheckInRequest {

    private UUID userId;

    private Date createdAt;

    @NotNull
    private Integer page;

    @NotNull
    private Integer size;
}
