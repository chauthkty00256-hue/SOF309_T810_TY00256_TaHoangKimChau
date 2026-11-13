package com.thestars.chatbox.controller;

import com.thestars.chatbox.service.FriendService;
import com.thestars.chatbox.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.security.Principal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FriendControllerTest {
    UserService users = mock(UserService.class);
    FriendController controller = new FriendController(mock(FriendService.class), users, mock(SimpMessagingTemplate.class));
    Principal principal = () -> "missing@example.com";

    @Test void listFriendsReturnsUnauthorizedWhenUserMissing() {
        when(users.findByEmail(principal.getName())).thenReturn(Optional.empty());
        assertEquals(401, controller.listFriends(principal).getStatusCode().value());
    }

    @Test void sendRequestRequiresAddresseeId() {
        when(users.findByEmail(principal.getName())).thenReturn(Optional.empty());
        assertEquals(401, controller.sendRequest(java.util.Map.of(), principal).getStatusCode().value());
    }
}
