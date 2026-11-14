package com.chhatkh.dev.services.impl;

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
import com.chhatkh.dev.services.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;

    @Value("${post.free.app.jwtExpirationMs}")
    private String expiredToken;

    @Override
    public ResponseEntity<?> authenticateUser(LoginReq req) {
        log.info("Login attempt for username {}", req != null ? req.getUsername() : null);

        if (req == null || !StringUtils.hasText(req.getUsername())
                || !StringUtils.hasText(req.getPassword())) {
            MessageRes messageRes = new MessageRes();
            messageRes.badRequest("Error: Invalid username and password");
            return new ResponseEntity<>(messageRes, HttpStatus.BAD_REQUEST);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String jwt = jwtUtils.generateJwtToken(userDetails);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("Authenticated user could not be loaded"));

            JwtRes jwtRes = new JwtRes();
            jwtRes.setExpiresIn(Integer.parseInt(expiredToken));
            jwtRes.setAccessToken(jwt);
            jwtRes.setRefreshToken(refreshToken.getToken());
            jwtRes.setTokenType(Constants.BEARER);
            jwtRes.setUser(user);

            return ResponseEntity.ok(jwtRes);
        } catch (AuthenticationException e) {
            log.error("Login failed for username {}", req.getUsername(), e);
            MessageRes messageRes = new MessageRes();
            messageRes.badRequest("Error: invalid username or password");
            return new ResponseEntity<>(messageRes, HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<MessageRes> logoutUser(LogOutReq req) {
        if (req == null || req.getUserId() == null) {
            MessageRes messageRes = new MessageRes();
            messageRes.badRequest("User ID is required for logout");
            return ResponseEntity.badRequest().body(messageRes);
        }
        refreshTokenService.deleteByUserId(req.getUserId());
        return ResponseEntity.ok(new MessageRes("Log out successful!", null));
    }

    @Override
    public ResponseEntity<JwtRes> refreshToken(TokenRefreshReq request) {
        String requestRefreshToken = request != null ? request.getRefreshToken() : null;
        if (!StringUtils.hasText(requestRefreshToken)) {
            throw new TokenRefreshException("", "Refresh token is required!");
        }
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
                    jwtRes.setUser(user);
                    return ResponseEntity.ok(jwtRes);
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }
}
