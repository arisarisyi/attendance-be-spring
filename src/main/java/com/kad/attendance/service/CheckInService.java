package com.kad.attendance.service;

import com.kad.attendance.config.JwtService;
import com.kad.attendance.entities.CheckIn;
import com.kad.attendance.entities.User;
import com.kad.attendance.model.CheckInRequest;
import com.kad.attendance.model.CheckInResponse;
import com.kad.attendance.model.UserResponse;
import com.kad.attendance.repository.CheckInRepository;
import com.kad.attendance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

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

            validation.validate(request);

        User existingUser = getUserFromJwtToken(jwtToken);

            CheckIn checkIn = new CheckIn();
            checkIn.setLatitude(request.getLatitude());
            checkIn.setLongitude(request.getLongitude());
            checkIn.setUser(existingUser);

            checkInRepository.save(checkIn);

        UserResponse userResponse = toUserResponse(existingUser);

            return toCheckInResponse(userResponse,checkIn);
    }

    private CheckInResponse toCheckInResponse(UserResponse userResponse, CheckIn checkIn){
        return CheckInResponse.builder()
                .id(checkIn.getId())
                .user(userResponse)
                .latitude(checkIn.getLatitude())
                .longitude(checkIn.getLongitude())
                .build();
    }

    @Transactional(readOnly = true)
    public CheckInResponse get(String jwtToken,int id){
        User existingUser = getUserFromJwtToken(jwtToken);

        CheckIn checkIn = checkInRepository.findFirstByUserAndId(existingUser,id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"check in not found"));

        UserResponse userResponse = toUserResponse(existingUser);

        return  toCheckInResponse(userResponse,checkIn);
    }

    private User getUserFromJwtToken(String jwtToken) {
        String npk = jwtService.extractNpk(jwtToken);

        return userRepository.findByNpk(npk)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User is not found")
                );
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .npk(user.getNpk())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().toString())
                .build();
    }

}
