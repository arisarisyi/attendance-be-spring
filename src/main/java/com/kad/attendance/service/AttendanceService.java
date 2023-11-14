package com.kad.attendance.service;

import com.kad.attendance.entities.CheckIn;
import com.kad.attendance.entities.CheckOut;
import com.kad.attendance.entities.User;
import com.kad.attendance.model.AttendanceResponse;
import com.kad.attendance.model.SearchAttendanceRequest;
import com.kad.attendance.model.UserResponse;
import com.kad.attendance.repository.CheckInRepository;
import com.kad.attendance.repository.CheckOutRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        Specification<CheckIn> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("user"), user));

            if (Objects.nonNull(request.getCreatedAt())) {
                Expression<Date> createdAtDate = builder.function("DATE", Date.class, root.get("createdAt"));
                predicates.add(builder.equal(createdAtDate, request.getCreatedAt()));
            }

            if (Objects.nonNull(request.getUserId())) {
                predicates.add(builder.equal(root.get("user").get("id"), request.getUserId()));
            }else {
                predicates.add(builder.equal(root.get("user").get("id"), user.getId()));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        UserResponse userResponse = toUserResponse(user);

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<CheckIn> checkIns = checkInRepository.findAll(specification, pageable);

        List<AttendanceResponse> checkInCheckoutResponses = checkIns.getContent().stream()
                .map(checkIn -> {
                    List<CheckOut> checkouts = checkOutRepository.findByUserIdAndCreatedAt(user.getId(),checkIn.getCreatedAt());

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
