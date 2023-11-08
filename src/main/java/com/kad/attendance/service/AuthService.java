package com.kad.attendance.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kad.attendance.config.JwtService;
import com.kad.attendance.entities.User;
import com.kad.attendance.model.LoginUserRequest;
import com.kad.attendance.model.TokenResponse;
import com.kad.attendance.repository.UserRepository;
import com.kad.attendance.security.BCrypt;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

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

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public TokenResponse login(LoginUserRequest request){

        validationService.validate(request);

        User user = userRepository.findByNpk(request.getNpk())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong"));

        if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {

            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            user.setToken(refreshToken);
            user.setTokenExpiredAt(next30Days());
            userRepository.save(user);

            return TokenResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .expiredAt(user.getTokenExpiredAt())
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong");
        }
    }

    private Long next30Days() {
        return System.currentTimeMillis() + refreshTokenExpiration;
    }

    public void logout(String jwtToken){
        String npk = jwtService.extractNpk(jwtToken);

        User existingUser = userRepository.findByNpk(npk).orElseThrow(()->new RuntimeException());

        existingUser.setToken(null);
        existingUser.setTokenExpiredAt(null);

        userRepository.save(existingUser);
    }

    public TokenResponse refreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userNpk;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Invalid or missing Authorization header");
        }

        refreshToken = authHeader.substring(7);
        userNpk = jwtService.extractNpk(refreshToken);
        if (userNpk != null) {
            UserDetails user = this.userRepository
                    .findByNpk(userNpk)
                    .orElseThrow();

            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                return TokenResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        throw new UnauthorizedException("Invalid refresh token");
    }

    public class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

}
