package com.chhatkh.dev.services;

import com.chhatkh.dev.payload.request.RegisterReq;
import com.chhatkh.dev.payload.response.MessageRes;
import com.chhatkh.dev.payload.response.RentHubRes.UserProfileRes;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<MessageRes> register(RegisterReq req);

    UserProfileRes getProfile(Long id);
}
