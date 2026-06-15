package com.tech.dimefresh.controller;


import com.tech.dimefresh.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/chat")
public class ChatController {
    private final ChatService chatService;

    @GetMapping
    public String showChat(Model model) {
        model.addAttribute("chat_id", chatService.getChatId());

        return "pages/chat";
    }
}
