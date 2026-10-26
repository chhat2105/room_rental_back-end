package com.chhatkh.dev.security;

import com.chhatkh.dev.dtos.UserDto;
import com.chhatkh.dev.entities.User;
import com.chhatkh.dev.exceptions.UnauthorizedException;
import com.chhatkh.dev.repositories.UserRepository;
import com.chhatkh.dev.services.AuthService;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;
    private final AuthService authService;

    public AuthUserArgumentResolver(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        Class<?> paramType = parameter.getParameterType();

        // 1. Direct match for CustomUserDetails principal
        if (CustomUserDetails.class.isAssignableFrom(paramType) && principal instanceof CustomUserDetails userDetails) {
            return userDetails;
        }

        // 2. Fetch User entity by ID or username
        User user;
        if (principal instanceof CustomUserDetails userDetails) {
            user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));
        } else {
            String username = authentication.getName();
            user = userRepository.findByUsernameOrEmail(username, username)
                    .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"));
        }

        // 3. Resolve based on requested target type
        if (User.class.isAssignableFrom(paramType)) {
            return user;
        }

        if (UserDto.class.isAssignableFrom(paramType)) {
            return authService.toDto(user);
        }

        if (Long.class.isAssignableFrom(paramType)) {
            return user.getId();
        }

        if (CustomUserDetails.class.isAssignableFrom(paramType)) {
            return CustomUserDetails.build(user);
        }

        return user;
    }
}
