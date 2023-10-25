package com.kad.attendance.service;

import com.kad.attendance.entities.User;
import com.kad.attendance.model.RegisterUserRequest;
import com.kad.attendance.repository.UserRepository;
import com.kad.attendance.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void register(RegisterUserRequest request){

        if (userRepository.existsById(request.getNpk())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"NPK already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setNpk(request.getNpk());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(request.getRole());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));

        userRepository.save(user);
    }
}
