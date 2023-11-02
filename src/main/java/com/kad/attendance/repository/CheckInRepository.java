package com.kad.attendance.repository;

import com.kad.attendance.entities.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CheckInRepository extends JpaRepository<CheckIn, String> {

    Optional<CheckIn> findFirstByUserId(UUID userId);
}
