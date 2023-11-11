package com.kad.attendance.service;

import com.kad.attendance.config.JwtService;
import com.kad.attendance.entities.CheckOut;
import com.kad.attendance.entities.User;
import com.kad.attendance.model.CheckOutRequest;
import com.kad.attendance.model.CheckOutResponse;
import com.kad.attendance.model.SearchCheckOutRequest;
import com.kad.attendance.model.UserResponse;
import com.kad.attendance.repository.CheckOutRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckOutService {

    @Autowired
    private CheckOutRepository checkOutRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ValidationService validation;


    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .npk(user.getNpk())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().toString())
                .build();
    }

    private CheckOutResponse toCheckOutResponse(UserResponse userResponse,
                                                CheckOut checkOut){
        return CheckOutResponse.builder()
                .id(checkOut.getId())
                .user(userResponse)
                .latitude(checkOut.getLatitude())
                .longitude(checkOut.getLongitude())
                .date(checkOut.getDate())
                .month(checkOut.getMonth())
                .year(checkOut.getYear())
                .time(checkOut.getTime())
                .build();
    }

    @Transactional
    public CheckOutResponse checkOut(User user, CheckOutRequest request){
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

        CheckOut checkOut = new CheckOut();
        checkOut.setLatitude(request.getLatitude());
        checkOut.setLongitude(request.getLongitude());
        checkOut.setDate(date);
        checkOut.setMonth(month);
        checkOut.setYear(year);
        checkOut.setTime(time);
        checkOut.setUser(user);

        checkOutRepository.save(checkOut);

        UserResponse userResponse = toUserResponse(user);

        return toCheckOutResponse(userResponse,checkOut);
    }

    @Transactional(readOnly = true)
    public CheckOutResponse get(Integer id, User user){

        CheckOut checkOut = checkOutRepository.findFirstByUserAndId(user,id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"check out not found"));

        UserResponse userResponse = toUserResponse(user);

        return toCheckOutResponse(userResponse, checkOut);
    }

    @Transactional(readOnly = true)
    public Page<CheckOutResponse> search(User user, SearchCheckOutRequest request){
        Specification<CheckOut> specification = ((root, query, builder) -> {
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
        });

        UserResponse userResponse = toUserResponse(user);

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<CheckOut> checkOuts = checkOutRepository.findAll(specification,pageable);
        List<CheckOutResponse> checkOutResponses =checkOuts.getContent().stream()
                .map(checkOut -> toCheckOutResponse(userResponse,checkOut))
                .collect(Collectors.toList());

        return new PageImpl<>(checkOutResponses,pageable,checkOuts.getTotalElements());
    }
}
