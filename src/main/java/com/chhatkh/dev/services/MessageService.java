package com.chhatkh.dev.services;

import com.chhatkh.dev.exception.*;
import com.chhatkh.dev.models.*;
import com.chhatkh.dev.models.enums.*;
import com.chhatkh.dev.payload.request.RentHubReq.*;
import com.chhatkh.dev.payload.response.RentHubRes.*;
import com.chhatkh.dev.repository.*;
import com.chhatkh.dev.security.CurrentUser;
import com.chhatkh.dev.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {
    private final MessageRepository messageRepo;
    private final UserRepository userRepo;
    private final CurrentUser currentUser;

    public ChatMessageRes send(MessageReq req) {
        User me = currentUser.get();
        if (me.getId().equals(req.receiverId())) throw new BadRequestException("You cannot message yourself");
        User to = userRepo.findById(req.receiverId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Message m = new Message();
        m.setSender(me); m.setReceiver(to); m.setMessage(req.message());
        return Mapper.message(messageRepo.save(m));
    }

    @Transactional(readOnly = true)
    public List<ChatMessageRes> conversation(Long otherUserId) {
        return messageRepo.conversation(currentUser.get().getId(), otherUserId).stream().map(Mapper::message).toList();
    }

    @Transactional(readOnly = true)
    public List<ContactRes> contacts() {
        return messageRepo.contacts(currentUser.get().getId()).stream()
                .map(u -> new ContactRes(u.getId(), Mapper.userName(u))).toList();
    }
}
