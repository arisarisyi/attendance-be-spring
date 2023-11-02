package com.kad.attendance.repository;

import com.kad.attendance.entities.CheckOut;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckOutRepository extends JpaRepository<CheckOut,String> {
}
