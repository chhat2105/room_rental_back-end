package com.chhatkh.dev.controller.rest.admin;

import com.chhatkh.dev.constants.Constants;
import com.chhatkh.dev.exception.TokenRefreshException;
import com.chhatkh.dev.models.RefreshToken;
import com.chhatkh.dev.models.Role;
import com.chhatkh.dev.models.User;
import com.chhatkh.dev.models.UserRole;
import com.chhatkh.dev.payload.request.LogOutReq;
import com.chhatkh.dev.payload.request.LoginReq;
import com.chhatkh.dev.payload.request.RegisterReq;
import com.chhatkh.dev.payload.request.TokenRefreshReq;
import com.chhatkh.dev.payload.response.JwtRes;
import com.chhatkh.dev.payload.response.MessageRes;
import com.chhatkh.dev.repository.RoleRepository;
import com.chhatkh.dev.repository.UserRepository;
import com.chhatkh.dev.security.jwt.JwtUtils;
import com.chhatkh.dev.security.services.RefreshTokenService;
import com.chhatkh.dev.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Authentication-only controller: login, logout and access-token refresh.
 * Registration, password-reset and OTP flows have been removed from this
 * controller (move them to a dedicated controller/service if still needed).
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "Authentication", description = "Login, logout and token refresh")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;



    @Value("${post.free.app.jwtExpirationMs}")
    private String expiredToken;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          JwtUtils jwtUtils,
                          RefreshTokenService refreshTokenService, PasswordEncoder encoder, RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
    }

    @Operation(summary = "Login", description = "Authenticate with phone number and password to receive a JWT access token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, returns JWT tokens"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid credentials"),
            @ApiResponse(responseCode = "401", description = "Invalid phone number or password")
    })
    @SecurityRequirements
    @PostMapping("/token")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginReq req) {

        log.info("Login attempt for username {}", req.getUsername());

        if (!StringUtils.hasText(req.getUsername())
                || !StringUtils.hasText(req.getPassword())) {

            MessageRes messageRes = new MessageRes();
            messageRes.badRequest("Error: Invalid username and password");

            return new ResponseEntity<>(
                    messageRes,
                    HttpStatus.BAD_REQUEST
            );
        }

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    req.getUsername(),
                                    req.getPassword()
                            )
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            UserDetailsImpl userDetails =
                    (UserDetailsImpl) authentication.getPrincipal();

            String jwt =
                    jwtUtils.generateJwtToken(userDetails);

            RefreshToken refreshToken =
                    refreshTokenService.createRefreshToken(
                            userDetails.getId()
                    );

            User user = userRepository
                    .findById(userDetails.getId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Authenticated user could not be loaded"
                            )
                    );

            // Never return the real password
            user.setPassword("******");

            JwtRes jwtRes = new JwtRes();

            jwtRes.setExpiresIn(
                    Integer.parseInt(expiredToken)
            );

            jwtRes.setAccessToken(jwt);
            jwtRes.setRefreshToken(refreshToken.getToken());
            jwtRes.setTokenType(Constants.BEARER);
            jwtRes.setUser(user);

            return ResponseEntity.ok(jwtRes);

        } catch (Exception e) {

            log.error(
                    "Login failed for username {}",
                    req.getUsername(),
                    e
            );

            MessageRes messageRes = new MessageRes();
            messageRes.badRequest(
                    "Error: invalid username or password"
            );

            return new ResponseEntity<>(
                    messageRes,
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    @Operation(summary = "Logout", description = "Invalidate the current refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody LogOutReq req) {
        refreshTokenService.deleteByUserId(req.getUserId());
        return ResponseEntity.ok(new MessageRes("Log out successful!", null));
    }

    @Operation(summary = "Register", description = "Create a new user account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or duplicate username / email / phone")
    })
    @SecurityRequirements
    @PostMapping("/register")
    public ResponseEntity<MessageRes> registerUser(@RequestBody RegisterReq req) {
        MessageRes messageRes = new MessageRes();
        log.info("Intercept create account req {}", req);
        try {
            if (req.getFirstName().equals("") || req.getLastName().equals("") || req.getPhoneNumber().equals("") || req.getEmail().equals("") || req.getPassword().equals("") || null == req.getPassword() || null == req.getConfirmPassword() || "".equals(req.getPassword()) || "".equals(req.getConfirmPassword())) {
                messageRes.badRequest("");
                return new ResponseEntity<>(messageRes, HttpStatus.BAD_REQUEST);
            }
            if (!req.getConfirmPassword().equals(req.getPassword())) {
                messageRes.setConfirmPasswordNotMatch();
                return new ResponseEntity<>(messageRes, HttpStatus.BAD_REQUEST);
            }
            if (userRepository.existsByUsernameAndStatus(req.getUsername(), Constants.STATUS_ACTIVE)) {
                messageRes.setNameAlreadyUse();
                return new ResponseEntity<>(messageRes, HttpStatus.BAD_REQUEST);
            }
            if (userRepository.existsByEmailAndStatus(req.getEmail(), Constants.STATUS_ACTIVE)) {
                messageRes.setEmailAlreadyUse();
                return new ResponseEntity<>(messageRes, HttpStatus.BAD_REQUEST);
            }
            if (userRepository.existsByPhoneNumberAndStatus(req.getPhoneNumber(), Constants.STATUS_ACTIVE)) {
                messageRes.setPhoneAlreadyUse();
                return new ResponseEntity<>(messageRes, HttpStatus.BAD_REQUEST);
            }
            User user = new User(req.getUsername(), req.getEmail(), encoder.encode(req.getPassword()), req.getPhoneNumber());
            Set<Role> roles = new HashSet<>();
            Role role = null;
            if (req.getRole().equals(UserRole.ROLE_USER) || req.getRole().equals("USER")) {
                role = roleRepository.findByName(UserRole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            }
            roles.add(role);
            user.setRoles(roles);
            user.setFirstName(req.getFirstName());
            user.setLastName(req.getLastName());
            user.setStatus(Constants.STATUS_ACTIVE);
            user.setChangePassword(Constants.No);
            user.setProfile(req.getProfile());
            userRepository.save(user);
            messageRes.setMessageCreateSuccess("User Open Account successfully!");
            return new ResponseEntity<>(messageRes, HttpStatus.OK);
        } catch (Throwable e) {
            log.info("Error open account req ", e);
            messageRes.internalServerError(null);
            return new ResponseEntity<>(messageRes, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            log.info("Open account req final result {}", messageRes);
        }
    }

    @Operation(summary = "Refresh token", description = "Get a new access token using a valid refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "New access token issued"),
            @ApiResponse(responseCode = "403", description = "Refresh token expired or not found")
    })
    @SecurityRequirements
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshReq request) {
        String requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    JwtRes jwtRes = new JwtRes();
                    jwtRes.setExpiresIn(Integer.parseInt(expiredToken));
                    jwtRes.setAccessToken(token);
                    jwtRes.setRefreshToken(requestRefreshToken);
                    jwtRes.setTokenType(Constants.BEARER);
                    user.setPassword("******");
                    jwtRes.setUser(user);
                    return ResponseEntity.ok(jwtRes);
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }
}