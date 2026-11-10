package com.chhatkh.dev.repository;

import com.chhatkh.dev.models.*;
import com.chhatkh.dev.models.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("select m from Message m where (m.sender.id = :a and m.receiver.id = :b) or (m.sender.id = :b and m.receiver.id = :a) order by m.createdAt asc")
    List<Message> conversation(@Param("a") Long a, @Param("b") Long b);

    @Query("select distinct case when m.sender.id = :uid then m.receiver else m.sender end from Message m where m.sender.id = :uid or m.receiver.id = :uid")
    List<User> contacts(@Param("uid") Long uid);
}
