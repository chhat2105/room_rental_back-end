package com.chhatkh.dev.repository;

import com.chhatkh.dev.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndStatus(String username, String status);

    Optional<User> findByPhoneNumberAndStatus(String phone, String status);

    Optional<User> findByPhoneNumberAndStatusAndChangePassword(String phone, String status,String changePassword);

    Boolean existsByUsernameAndStatus(String username, String status);

    Boolean existsByEmailAndStatus(String email, String status);

    Boolean existsByPhoneNumberAndStatus(String phone, String status);


    List<User> findAllByStatus(String status);

    Optional<User> findByUsername(String username);

    User findByIdAndStatusIn(Long id, List<String> stringList);
}
