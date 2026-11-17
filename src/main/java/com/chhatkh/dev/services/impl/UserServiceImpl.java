package com.chhatkh.dev.services.impl;

import com.chhatkh.dev.constants.Constants;
import com.chhatkh.dev.models.Role;
import com.chhatkh.dev.models.User;
import com.chhatkh.dev.models.UserRole;
import com.chhatkh.dev.payload.request.RegisterReq;
import com.chhatkh.dev.payload.response.MessageRes;
import com.chhatkh.dev.payload.response.RentHubRes.UserProfileRes;
import com.chhatkh.dev.repository.RoleRepository;
import com.chhatkh.dev.repository.UserRepository;
import com.chhatkh.dev.services.UserProfileService;
import com.chhatkh.dev.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileService userProfileService;

    @Override
    @Transactional
    public ResponseEntity<MessageRes> register(RegisterReq req) {
        MessageRes response = new MessageRes();
        if (req == null || !StringUtils.hasText(req.getFirstName())
                || !StringUtils.hasText(req.getLastName())
                || !StringUtils.hasText(req.getUsername())
                || !StringUtils.hasText(req.getPhoneNumber())
                || !StringUtils.hasText(req.getEmail())
                || !StringUtils.hasText(req.getPassword())
                || !StringUtils.hasText(req.getConfirmPassword())) {
            response.badRequest("Required registration fields are missing");
            return ResponseEntity.badRequest().body(response);
        }
        if (!req.getConfirmPassword().equals(req.getPassword())) {
            response.setConfirmPasswordNotMatch();
            return ResponseEntity.badRequest().body(response);
        }
        if (Boolean.TRUE.equals(userRepository.existsByUsernameAndStatus(req.getUsername(), Constants.STATUS_ACTIVE))) {
            response.setNameAlreadyUse();
            return ResponseEntity.badRequest().body(response);
        }
        if (Boolean.TRUE.equals(userRepository.existsByEmailAndStatus(req.getEmail(), Constants.STATUS_ACTIVE))) {
            response.setEmailAlreadyUse();
            return ResponseEntity.badRequest().body(response);
        }
        if (Boolean.TRUE.equals(userRepository.existsByPhoneNumberAndStatus(req.getPhoneNumber(), Constants.STATUS_ACTIVE))) {
            response.setPhoneAlreadyUse();
            return ResponseEntity.badRequest().body(response);
        }

        UserRole targetRole = resolveRole(req.getRole());
        if (targetRole == null || targetRole == UserRole.ROLE_ADMIN) {
            response.badRequest("Unsupported user role");
            return ResponseEntity.badRequest().body(response);
        }

        Role role = roleRepository.findByName(targetRole)
                .orElseGet(() -> roleRepository.save(new Role(targetRole)));

        User user = new User(req.getUsername(), req.getEmail(),
                passwordEncoder.encode(req.getPassword()), req.getPhoneNumber());
        user.setRoles(Set.of(role));
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setStatus(Constants.STATUS_ACTIVE);
        user.setChangePassword(Constants.No);
        user.setProfile(req.getProfile());
        userRepository.save(user);

        response.setMessageCreateSuccess("User Open Account successfully!");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileRes getProfile(Long id) {
        return userProfileService.get(id);
    }

    private UserRole resolveRole(String roleInput) {
        if (!StringUtils.hasText(roleInput) || "string".equalsIgnoreCase(roleInput.trim())) {
            return UserRole.ROLE_USER;
        }
        String normalized = roleInput.trim().toUpperCase(Locale.ROOT);
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }
        try {
            return UserRole.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
