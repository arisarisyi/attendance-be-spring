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
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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

            CheckIn checkIn = new CheckIn();
            checkIn.setLatitude(request.getLatitude());
            checkIn.setLongitude(request.getLongitude());
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
                .build();
    }

    @Transactional(readOnly = true)
    public CheckInResponse get(Integer id, User user){

        CheckIn checkIn = checkInRepository.findFirstByUserAndId(user,id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"check in not found"));

        UserResponse userResponse = toUserResponse(user);

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

//    @Transactional(readOnly = true)
//    public Page<CheckInResponse> search(User user, SearchCheckInRequest request){
//        Specification<CheckIn> specification = (root, query, builder)->{
//           List<Predicate> predicates= new ArrayList<>();
//           predicates.add(builder.equal(root.get("user"),user));
//
//           if(Objects.nonNull(request.getCreatedAt())){
//               predicates.add(builder.like(root.get("createdAt"),"%"+request.getCreatedAt()+"%"));
//           }
//
//           if(Objects.nonNull(request.getUserId())){
//               predicates.add(builder.like(root.get("userId"),"%"+request.getUserId()+"%"));
//           }
//
//           return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
//        };
//
//        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
//        Page<CheckIn> checkIns = checkInRepository.findAll(specification,pageable);
//        List<CheckInResponse> checkInResponses = checkIns.getContent().stream()
//                .map(checkIn -> toCheckInResponse(checkIn))
//                .collect(Collectors.toList());
//    }

}
