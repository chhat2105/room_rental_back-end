package com.chhatkh.dev.controller.rest;

import com.chhatkh.dev.models.enums.*;
import com.chhatkh.dev.payload.request.RentHubReq.*;
import com.chhatkh.dev.payload.response.RentHubRes.*;
import com.chhatkh.dev.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ChatMessageRes send(@Valid @RequestBody MessageReq req) { return service.send(req); }

    @GetMapping("/with/{userId}")
    public List<ChatMessageRes> conversation(@PathVariable Long userId) { return service.conversation(userId); }

    @GetMapping("/contacts")
    public List<ContactRes> contacts() { return service.contacts(); }
}
