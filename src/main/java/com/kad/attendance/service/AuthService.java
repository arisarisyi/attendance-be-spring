package com.kad.attendance.service;

import com.kad.attendance.config.JwtService;
import com.kad.attendance.entities.User;
import com.kad.attendance.model.LoginUserRequest;
import com.kad.attendance.model.TokenResponse;
import com.kad.attendance.repository.UserRepository;
import com.kad.attendance.security.BCrypt;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ValidationService validationService;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final AuthenticationManager authenticationManager;

    public TokenResponse login(LoginUserRequest request){

        validationService.validate(request);

//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getNpk(),
//                        request.getPassword()
//                )
//        );

        User user = userRepository.findByNpk(request.getNpk())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong"));

//        var jwtToken = jwtService.generateToken(user);
//        user.setToken(jwtToken);
//        user.setTokenExpiredAt(next30Days());
//        userRepository.save(user);
//
//        return TokenResponse.builder()
//                .token(jwtToken)
//                .expiredAt(user.getTokenExpiredAt())
//                .build();

        if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {

            var jwtToken = jwtService.generateToken(user);
            user.setToken(jwtToken);
            user.setTokenExpiredAt(next30Days());
            userRepository.save(user);

            return TokenResponse.builder()
                    .token(jwtToken)
                    .expiredAt(user.getTokenExpiredAt())
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong");
        }
    }

    private Long next30Days() {
        return System.currentTimeMillis() + (1000 * 16 * 24 * 30);
    }
}
