package com.kad.attendance.repository;

import com.kad.attendance.entities.CheckOut;
import com.kad.attendance.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CheckOutRepository extends JpaRepository<CheckOut,String>, JpaSpecificationExecutor<CheckOut> {
    Optional<CheckOut> findFirstByUserAndId(User user,Integer id);

    @Query("SELECT co FROM CheckOut co WHERE co.user.id = :userId AND DATE(co.createdAt) = DATE(:createdAt)")
    List<CheckOut> findByUserIdAndCreatedAt(@Param("userId") UUID userId, @Param("createdAt") Date createdAt);
}
