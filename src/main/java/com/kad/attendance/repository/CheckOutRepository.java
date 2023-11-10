package com.kad.attendance.repository;

import com.kad.attendance.entities.CheckOut;
import com.kad.attendance.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CheckOutRepository extends JpaRepository<CheckOut,String>, JpaSpecificationExecutor<CheckOut> {
    Optional<CheckOut> findFirstByUserAndId(User user,Integer id);
}
