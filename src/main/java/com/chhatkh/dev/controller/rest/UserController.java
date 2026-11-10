package com.chhatkh.dev.controller.rest;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User registration and profiles")
public class UserController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileService profileService;

    @Operation(summary = "Register", description = "Create a new user account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or duplicate username / email / phone")
    })
    @SecurityRequirements
    @PostMapping
    public ResponseEntity<MessageRes> register(@RequestBody RegisterReq req) {
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
        if (userRepository.existsByUsernameAndStatus(req.getUsername(), Constants.STATUS_ACTIVE)) {
            response.setNameAlreadyUse();
            return ResponseEntity.badRequest().body(response);
        }
        if (userRepository.existsByEmailAndStatus(req.getEmail(), Constants.STATUS_ACTIVE)) {
            response.setEmailAlreadyUse();
            return ResponseEntity.badRequest().body(response);
        }
        if (userRepository.existsByPhoneNumberAndStatus(req.getPhoneNumber(), Constants.STATUS_ACTIVE)) {
            response.setPhoneAlreadyUse();
            return ResponseEntity.badRequest().body(response);
        }

        UserRole targetRole = UserRole.ROLE_TENANT;
        if (StringUtils.hasText(req.getRole())) {
            String r = req.getRole().toUpperCase().trim();
            if (r.contains("LANDLORD")) {
                targetRole = UserRole.ROLE_LANDLORD;
            } else if (r.contains("ADMIN")) {
                targetRole = UserRole.ROLE_ADMIN;
            } else if (r.contains("USER")) {
                targetRole = UserRole.ROLE_USER;
            } else {
                targetRole = UserRole.ROLE_TENANT;
            }
        }

        UserRole finalTargetRole = targetRole;
        Role role = roleRepository.findByName(finalTargetRole)
                .orElseGet(() -> roleRepository.save(new Role(finalTargetRole)));
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

    @Operation(summary = "Get user profile")
    @GetMapping("/{id}/profile")
    public UserProfileRes profile(@PathVariable Long id) {
        return profileService.get(id);
    }
}
