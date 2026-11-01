package com.thonsokvechey.renthubapi.security;

import com.thonsokvechey.renthubapi.exception.*;
import com.thonsokvechey.renthubapi.models.User;
import com.thonsokvechey.renthubapi.repository.UserRepository;
import com.thonsokvechey.renthubapi.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUser {
    private final UserRepository userRepository;

    public boolean hasRole(String role) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    public User get() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl d))
            throw new ForbiddenException("Not authenticated");
        return userRepository.findById(d.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
