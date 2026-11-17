package com.chhatkh.dev.controller.rest;

import com.chhatkh.dev.payload.request.RegisterReq;
import com.chhatkh.dev.payload.response.MessageRes;
import com.chhatkh.dev.payload.response.RentHubRes.UserProfileRes;
import com.chhatkh.dev.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User registration and profiles")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Register", description = "Create a new user account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or duplicate username / email / phone")
    })
    @SecurityRequirements
    @PostMapping({"", "/register"})
    public ResponseEntity<MessageRes> register(@RequestBody RegisterReq req) {
        return userService.register(req);
    }

    @Operation(summary = "Get user profile")
    @GetMapping("/{id}/profile")
    public UserProfileRes profile(@PathVariable Long id) {
        return userService.getProfile(id);
    }
}
