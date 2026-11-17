package com.chhatkh.dev.controller.rest;

import com.chhatkh.dev.payload.request.LogOutReq;
import com.chhatkh.dev.payload.request.LoginReq;
import com.chhatkh.dev.payload.request.RegisterReq;
import com.chhatkh.dev.payload.request.TokenRefreshReq;
import com.chhatkh.dev.payload.response.MessageRes;
import com.chhatkh.dev.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller: login, register, logout and access-token refresh.
 */
@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login, register, logout and token refresh")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Login", description = "Authenticate with username/phone/email and password to receive a JWT access token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, returns JWT tokens"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid credentials"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    @SecurityRequirements
    @PostMapping({ "/token", "/login" })
    public ResponseEntity<?> authenticateUser(@RequestBody LoginReq req) {
        return authService.authenticateUser(req);
    }

    @Operation(summary = "Register", description = "Create a new user account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or duplicate username / email / phone")
    })
    @SecurityRequirements
    @PostMapping("/register")
    public ResponseEntity<MessageRes> registerUser(@RequestBody RegisterReq req) {
        return authService.registerUser(req);
    }

    @Operation(summary = "Logout", description = "Invalidate the current refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody(required = false) LogOutReq req) {
        return authService.logoutUser(req);
    }

    @Operation(summary = "Refresh token", description = "Get a new access token using a valid refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "New access token issued"),
            @ApiResponse(responseCode = "403", description = "Refresh token expired or not found")
    })
    @SecurityRequirements
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshReq request) {
        return authService.refreshToken(request);
    }
}
