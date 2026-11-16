package com.chhatkh.dev.services;

import com.chhatkh.dev.payload.request.RentHubReq.MessageReq;
import com.chhatkh.dev.payload.response.RentHubRes.ChatMessageRes;
import com.chhatkh.dev.payload.response.RentHubRes.ContactRes;

import java.util.List;

public interface MessageService {
    ChatMessageRes send(MessageReq req);

    List<ChatMessageRes> conversation(Long otherUserId);

    List<ContactRes> contacts();
}
