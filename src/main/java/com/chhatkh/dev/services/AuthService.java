package com.chhatkh.dev.services;

import com.chhatkh.dev.payload.request.LogOutReq;
import com.chhatkh.dev.payload.request.LoginReq;
import com.chhatkh.dev.payload.request.TokenRefreshReq;
import com.chhatkh.dev.payload.response.JwtRes;
import com.chhatkh.dev.payload.response.MessageRes;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> authenticateUser(LoginReq req);

    ResponseEntity<MessageRes> logoutUser(LogOutReq req);

    ResponseEntity<JwtRes> refreshToken(TokenRefreshReq request);
}
