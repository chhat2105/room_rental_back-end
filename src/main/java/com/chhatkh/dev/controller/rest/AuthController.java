package com.chhatkh.dev.controller.rest;

import com.chhatkh.dev.constants.Constants;
import com.chhatkh.dev.exception.TokenRefreshException;
import com.chhatkh.dev.models.RefreshToken;
import com.chhatkh.dev.models.User;
import com.chhatkh.dev.payload.request.LogOutReq;
import com.chhatkh.dev.payload.request.LoginReq;
import com.chhatkh.dev.payload.request.TokenRefreshReq;
import com.chhatkh.dev.payload.response.JwtRes;
import com.chhatkh.dev.payload.response.MessageRes;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication-only controller: login, logout and access-token refresh.
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
    @Value("${post.free.app.jwtExpirationMs}")
    private String expiredToken;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          JwtUtils jwtUtils,
                          RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
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

        } catch (AuthenticationException e) {

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