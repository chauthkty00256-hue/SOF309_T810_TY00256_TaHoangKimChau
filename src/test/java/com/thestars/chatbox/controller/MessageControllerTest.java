package com.thestars.chatbox.controller;

import com.thestars.chatbox.config.PresenceTracker;
import com.thestars.chatbox.service.ChatService;
import com.thestars.chatbox.service.MessageService;
import com.thestars.chatbox.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import static org.mockito.Mockito.*;

class MessageControllerTest {
    MessageService messages = mock(MessageService.class);
    ChatService chats = mock(ChatService.class);
    UserService users = mock(UserService.class);
    SimpMessagingTemplate messaging = mock(SimpMessagingTemplate.class);
    PresenceTracker presence = mock(PresenceTracker.class);
    MessageController controller = new MessageController(messages, chats, users, messaging, presence);

    @Test void unauthenticatedMessageIsIgnored() {
        controller.sendMessage(2L, java.util.Map.of("content", "hello"), null);
        verifyNoInteractions(messages, chats, messaging);
    }

    @Test void unauthenticatedPingIsIgnored() {
        controller.ping(null);
        verifyNoInteractions(presence);
    }
}
