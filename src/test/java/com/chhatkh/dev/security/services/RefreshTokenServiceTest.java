package com.chhatkh.dev.security.services;

import com.chhatkh.dev.models.RefreshToken;
import com.chhatkh.dev.models.User;
import com.chhatkh.dev.repository.RefreshTokenRepository;
import com.chhatkh.dev.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 60_000L);
    }

    @Test
    void createRefreshTokenReplacesPreviousTokenForUser() {
        long userId = 42L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(org.mockito.ArgumentMatchers.any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken token = refreshTokenService.createRefreshToken(userId);

        InOrder calls = inOrder(refreshTokenRepository);
        calls.verify(refreshTokenRepository).deleteByUserId(userId);
        calls.verify(refreshTokenRepository).save(token);
        assertNotNull(token.getToken());
        assertNotNull(token.getExpiryDate());
        verify(userRepository).findById(userId);
    }
}
