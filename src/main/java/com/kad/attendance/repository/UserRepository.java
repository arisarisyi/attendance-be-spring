package com.kad.attendance.repository;

import com.kad.attendance.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findByNpk(String npk);
    User findOneByNpk(String npk);

    Optional<User> findFirstByToken(String token);
}
