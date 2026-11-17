package com.chhatkh.dev.repository;

import com.chhatkh.dev.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.status = :status ORDER BY u.id DESC FETCH FIRST 1 ROWS ONLY")
    Optional<User> findByUsernameAndStatus(@Param("username") String username, @Param("status") String status);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.status = :status ORDER BY u.id DESC FETCH FIRST 1 ROWS ONLY")
    Optional<User> findByEmailAndStatus(@Param("email") String email, @Param("status") String status);

    @Query("SELECT u FROM User u WHERE u.phoneNumber = :phone AND u.status = :status ORDER BY u.id DESC FETCH FIRST 1 ROWS ONLY")
    Optional<User> findByPhoneNumberAndStatus(@Param("phone") String phone, @Param("status") String status);

    @Query("SELECT u FROM User u WHERE u.phoneNumber = :phone AND u.status = :status AND u.changePassword = :changePassword ORDER BY u.id DESC FETCH FIRST 1 ROWS ONLY")
    Optional<User> findByPhoneNumberAndStatusAndChangePassword(@Param("phone") String phone, @Param("status") String status, @Param("changePassword") String changePassword);

    Boolean existsByUsernameAndStatus(String username, String status);

    Boolean existsByEmailAndStatus(String email, String status);

    Boolean existsByPhoneNumberAndStatus(String phone, String status);

    List<User> findAllByStatus(String status);

    @Query("SELECT u FROM User u WHERE u.username = :username ORDER BY u.id DESC FETCH FIRST 1 ROWS ONLY")
    Optional<User> findByUsername(@Param("username") String username);

    User findByIdAndStatusIn(Long id, List<String> stringList);
}
