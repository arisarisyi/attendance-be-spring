package com.kad.attendance.repository;

import com.kad.attendance.entities.CheckIn;
import com.kad.attendance.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CheckInRepository extends JpaRepository<CheckIn, String>, JpaSpecificationExecutor<CheckIn> {

    Optional<CheckIn> findFirstByUserId(UUID userId);

    List<CheckIn> findAllByUserId(UUID userId);

    Optional<CheckIn> findFirstByUserAndId(User user, int id);


}
