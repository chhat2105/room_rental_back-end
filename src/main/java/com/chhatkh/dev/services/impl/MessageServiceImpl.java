package com.chhatkh.dev.services.impl;

import com.chhatkh.dev.exception.BadRequestException;
import com.chhatkh.dev.exception.ResourceNotFoundException;
import com.chhatkh.dev.models.Message;
import com.chhatkh.dev.models.User;
import com.chhatkh.dev.payload.request.RentHubReq.MessageReq;
import com.chhatkh.dev.payload.response.RentHubRes.ChatMessageRes;
import com.chhatkh.dev.payload.response.RentHubRes.ContactRes;
import com.chhatkh.dev.repository.MessageRepository;
import com.chhatkh.dev.repository.UserRepository;
import com.chhatkh.dev.security.CurrentUser;
import com.chhatkh.dev.services.MessageService;
import com.chhatkh.dev.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepo;
    private final UserRepository userRepo;
    private final CurrentUser currentUser;

    @Override
    public ChatMessageRes send(MessageReq req) {
        User me = currentUser.get();
        if (me.getId().equals(req.receiverId())) throw new BadRequestException("You cannot message yourself");
        User to = userRepo.findById(req.receiverId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Message m = new Message();
        m.setSender(me);
        m.setReceiver(to);
        m.setMessage(req.message());
        return Mapper.message(messageRepo.save(m));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageRes> conversation(Long otherUserId) {
        return messageRepo.conversation(currentUser.get().getId(), otherUserId).stream().map(Mapper::message).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactRes> contacts() {
        return messageRepo.contacts(currentUser.get().getId()).stream()
                .map(u -> new ContactRes(u.getId(), Mapper.userName(u))).toList();
    }
}
