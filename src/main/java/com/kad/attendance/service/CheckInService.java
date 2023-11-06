package com.kad.attendance.service;

import com.kad.attendance.config.JwtService;
import com.kad.attendance.entities.CheckIn;
import com.kad.attendance.entities.User;
import com.kad.attendance.model.CheckInRequest;
import com.kad.attendance.model.CheckInResponse;
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

    @Autowired
    private ValidationService validation;

    @Transactional
    public CheckInResponse checkIn(String jwtToken, CheckInRequest request){
        try{
            validation.validate(request);

            String npk = jwtService.extractNpk(jwtToken);

            User existingUser = userRepository.findByNpk(npk).orElseThrow(
                    ()->new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "User is not found")
            );

            CheckIn checkIn = new CheckIn();
            checkIn.setLatitude(request.getLatitude());
            checkIn.setLongitude(request.getLongitude());
            checkIn.setUser(existingUser);

            checkInRepository.save(checkIn);

            return CheckInResponse.builder()
                    .id(checkIn.getId())
                    .user(checkIn.getUser())
                    .latitude(checkIn.getLatitude())
                    .longitude(checkIn.getLongitude())
                    .build();

        }
        catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public CheckInResponse get(CheckIn checkIn) {
        return CheckInResponse.builder()
                .id(checkIn.getId())
                .user(checkIn.getUser())
                .latitude(checkIn.getLatitude())
                .longitude(checkIn.getLongitude())
                .build();
    }
}
