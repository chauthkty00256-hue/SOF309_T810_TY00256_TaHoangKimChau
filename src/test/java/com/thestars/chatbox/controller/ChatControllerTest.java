package com.thestars.chatbox.controller;

import com.thestars.chatbox.service.ChatService;
import com.thestars.chatbox.service.MessageService;
import com.thestars.chatbox.service.UserService;
import org.junit.jupiter.api.Test;
import java.security.Principal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ChatControllerTest {
    UserService users = mock(UserService.class);
    ChatController controller = new ChatController(mock(ChatService.class), mock(MessageService.class), users);
    Principal principal = () -> "missing@example.com";

    @Test void listConversationsReturnsUnauthorizedWhenUserMissing() {
        when(users.findByEmail(principal.getName())).thenReturn(Optional.empty());
        assertEquals(401, controller.listConversations(principal).getStatusCode().value());
    }

    @Test void availableGroupsReturnsUnauthorizedWhenUserMissing() {
        when(users.findByEmail(principal.getName())).thenReturn(Optional.empty());
        assertEquals(401, controller.getAvailableGroups(principal).getStatusCode().value());
    }
}
