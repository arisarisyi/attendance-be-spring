package com.kad.attendance.service;

import com.kad.attendance.config.JwtService;
import com.kad.attendance.entities.User;
import com.kad.attendance.model.RegisterUserRequest;
import com.kad.attendance.model.UserResponse;
import com.kad.attendance.repository.UserRepository;
import com.kad.attendance.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Transactional
    public void register(RegisterUserRequest request){

        Optional<User> existingUserOptional = userRepository.findByNpk(request.getNpk());

        if (existingUserOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NPK already registered");
        }

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setNpk(request.getNpk());
        user.setRole(request.getRole());
        var jwtToken = jwtService.generateToken(user);
        user.setToken(jwtToken);

        userRepository.save(user);
    }

    public UserResponse get(User user) {
        return UserResponse.builder()
                .npk(user.getNpk())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getNpk())
                .build();
    }
}
