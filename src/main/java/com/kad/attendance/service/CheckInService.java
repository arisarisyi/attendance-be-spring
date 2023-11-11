package com.kad.attendance.service;

import com.kad.attendance.config.JwtService;
import com.kad.attendance.entities.CheckIn;
import com.kad.attendance.entities.User;
import com.kad.attendance.model.CheckInRequest;
import com.kad.attendance.model.CheckInResponse;
import com.kad.attendance.model.SearchCheckInRequest;
import com.kad.attendance.model.UserResponse;
import com.kad.attendance.repository.CheckInRepository;
import com.kad.attendance.repository.UserRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public CheckInResponse checkIn(User user, CheckInRequest request){

            validation.validate(request);

        LocalDateTime localDateTime = LocalDateTime.now();

        // Menggunakan DateTimeFormatter untuk format khusus
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Mendapatkan nilai-nilai yang diinginkan
        String date = localDateTime.format(dateFormatter);
        String month = localDateTime.format(monthFormatter);
        String year = localDateTime.format(yearFormatter);
        String time = localDateTime.format(timeFormatter);

            CheckIn checkIn = new CheckIn();
            checkIn.setLatitude(request.getLatitude());
            checkIn.setLongitude(request.getLongitude());
            checkIn.setDate(date);
            checkIn.setMonth(month);
            checkIn.setYear(year);
            checkIn.setTime(time);
            checkIn.setUser(user);

            checkInRepository.save(checkIn);

            UserResponse userResponse = toUserResponse(user);

            return toCheckInResponse(userResponse,checkIn);
    }

    private CheckInResponse toCheckInResponse(UserResponse userResponse, CheckIn checkIn){
        return CheckInResponse.builder()
                .id(checkIn.getId())
                .user(userResponse)
                .latitude(checkIn.getLatitude())
                .longitude(checkIn.getLongitude())
                .date(checkIn.getDate())
                .month(checkIn.getMonth())
                .year(checkIn.getYear())
                .time(checkIn.getTime())
                .build();
    }

    @Transactional(readOnly = true)
    public CheckInResponse get(Integer id, User user){

        CheckIn checkIn = checkInRepository.findFirstByUserAndId(user,id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"check in not found"));

        UserResponse userResponse = toUserResponse(user);

//        // Mendapatkan waktu penciptaan CheckIn dari database
//        Date timeYesterday = checkIn.getCreatedAt();
//
//        // Konversi Date ke LocalDateTime
//        LocalDateTime localDateTimeYesterday = timeYesterday.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//
//        // Mendapatkan instance dari LocalDateTime untuk waktu sekarang
//        LocalDateTime timeNow = LocalDateTime.now();
//
//        // Menampilkan hasil
//        System.out.println("Waktu Sekarang: " + timeNow);
//        System.out.println("Waktu Kemarin: " + localDateTimeYesterday);
//
//        // Menghitung perbedaan jam antara waktu sekarang dan waktu kemarin
//        long perbedaanJam = ChronoUnit.HOURS.between(localDateTimeYesterday, timeNow);
//
//        System.out.println("Perbedaan Jam: " + perbedaanJam + " jam");

        return  toCheckInResponse(userResponse,checkIn);
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

    @Transactional(readOnly = true)
    public Page<CheckInResponse> search(User user, SearchCheckInRequest request){
        Specification<CheckIn> specification = (root, query, builder)->{
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("user"), user));

            if(Objects.nonNull(request.getCreatedAt())){
                Expression<Date> createdAtDate = builder.function("DATE", Date.class, root.get("createdAt"));
                predicates.add(builder.equal(createdAtDate, request.getCreatedAt()));
            }

            if(user.getRole().toString() == "SUPERADMIN"){
                if (Objects.nonNull(request.getUserId())) {
                    predicates.add(builder.equal(root.get("user").get("id"), request.getUserId()));
                }
            } else {
                throw new RuntimeException("Unauthorized");
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        UserResponse userResponse = toUserResponse(user);

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<CheckIn> checkIns = checkInRepository.findAll(specification,pageable);
        List<CheckInResponse> checkInResponses = checkIns.getContent().stream()
                .map(checkIn -> toCheckInResponse(userResponse,checkIn))
                .collect(Collectors.toList());

        return new PageImpl<>(checkInResponses, pageable, checkIns.getTotalElements());
    }
}
