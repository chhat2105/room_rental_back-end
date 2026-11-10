package com.chhatkh.dev.security.services;

import com.chhatkh.dev.constants.Constants;
import com.chhatkh.dev.models.User;
import com.chhatkh.dev.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByUsernameAndStatus(
                        username,
                        Constants.STATUS_ACTIVE
                )
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User Not Found with username: " + username
                        )
                );

        return UserDetailsImpl.build(user);
    }
}
