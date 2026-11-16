package com.chhatkh.dev.services;

import com.chhatkh.dev.payload.response.RentHubRes.UserProfileRes;

public interface UserProfileService {
    UserProfileRes get(Long id);
}
