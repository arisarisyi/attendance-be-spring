package com.kad.attendance.service;

import com.kad.attendance.entities.CheckIn;
import com.kad.attendance.entities.CheckOut;
import com.kad.attendance.entities.User;
import com.kad.attendance.model.AttendanceResponse;
import com.kad.attendance.model.SearchAttendanceRequest;
import com.kad.attendance.model.UserResponse;
import com.kad.attendance.repository.CheckInRepository;
import com.kad.attendance.repository.CheckOutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private CheckOutRepository checkOutRepository;

    @Transactional(readOnly = true)
    public Page<AttendanceResponse> search(User user, SearchAttendanceRequest request) {
        UUID userIdToUse = Objects.nonNull(request.getUserId()) ? request.getUserId() : user.getId();

        UserResponse userResponse = toUserResponse(user);

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<CheckIn> checkIns = checkInRepository.findAllByUserId(userIdToUse,pageable);

        List<AttendanceResponse> checkInCheckoutResponses = checkIns.getContent().stream()
                .map(checkIn -> {
                    List<CheckOut> checkouts = checkOutRepository.findByUserIdAndCreatedAt(userIdToUse,checkIn.getCreatedAt());

                    CheckOut checkout = checkouts.stream()
                            .filter(co -> co.getCreatedAt() != null)
                            .max(Comparator.comparing(CheckOut::getCreatedAt))
                            .orElse(null);

                    if (checkout != null && checkout.getCreatedAt() != null) {

                        Duration differentTime = calculateDifferentTime(
                                checkIn.getCreatedAt(), checkout.getCreatedAt()
                        );

                        return new AttendanceResponse(
                                userResponse,
                                checkIn.getCreatedAt(),
                                checkout.getCreatedAt(),
                                formatDuration(differentTime)
                        );

                    } else {
                        return new AttendanceResponse(userResponse, checkIn.getCreatedAt(), null, "0");
                    }
                })
                .collect(Collectors.toList());

        return new PageImpl<>(checkInCheckoutResponses, pageable, checkIns.getTotalElements());
    }

    private Duration calculateDifferentTime(Date checkInTime, Date checkOutTime) {
        if (checkInTime != null && checkOutTime != null) {
            LocalDateTime checkInLocalDateTime = LocalDateTime.ofInstant(checkInTime.toInstant(), ZoneId.systemDefault());
            LocalDateTime checkOutLocalDateTime = LocalDateTime.ofInstant(checkOutTime.toInstant(), ZoneId.systemDefault());
            return Duration.between(checkInLocalDateTime, checkOutLocalDateTime).abs();
        } else {
            return Duration.ZERO;
        }
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        return String.format("%d jam %d menit", hours, minutes);
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .npk(user.getNpk())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().toString())
                .build();
    }
}
