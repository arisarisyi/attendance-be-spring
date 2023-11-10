package com.kad.attendance.resolver;

import com.kad.attendance.config.JwtService;
import com.kad.attendance.entities.User;
import com.kad.attendance.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;



    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return User.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        String token = servletRequest.getHeader("Authorization");
        String jwtToken = token.substring(7);

        String npk = jwtService.extractNpk(jwtToken);
        boolean tokenExp = jwtService.isTokenExpired(jwtToken);

        if(token == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized");
        }

        User user = userRepository.findByNpk(npk)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unauthorized"));

        if (tokenExp == true) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return user;
    }
}
