package com.kad.attendance.service;

import com.kad.attendance.config.JwtService;
import com.kad.attendance.entities.CheckIn;
import com.kad.attendance.entities.User;
import com.kad.attendance.model.CheckInRequest;
import com.kad.attendance.model.UserResponse;
import com.kad.attendance.repository.CheckInRepository;
import com.kad.attendance.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckInService {
    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Transactional
    public void checkIn(String jwtToken){
        try{
            String npk = jwtService.extractNpk(jwtToken);

            Optional<User> checkUserOptional = userRepository.findByNpk(npk);

            // Sekarang Anda memiliki seluruh data pengguna dalam objek 'checkUser'

            Optional<User> checkUser = userRepository.findByNpk(npk);
            User checkUser1 = checkUser.get();

            var iniNpk = this.get(checkUser1).getNpk();
            System.out.println(iniNpk);

            if(!checkUser.isPresent()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee not exists");
            }

        }
        catch(Exception e){
            throw new RuntimeException();
        }
    }

    public UserResponse get(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .npk(user.getNpk())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .build();
    }
}
