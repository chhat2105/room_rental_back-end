package com.chhatkh.dev.security;

import com.chhatkh.dev.exception.*;
import com.chhatkh.dev.models.User;
import com.chhatkh.dev.repository.UserRepository;
import com.chhatkh.dev.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUser {
    private final UserRepository userRepository;

    public User get() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl d))
            throw new ForbiddenException("Not authenticated");
        return userRepository.findById(d.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
