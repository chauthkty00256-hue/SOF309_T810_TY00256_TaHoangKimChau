package com.thestars.chatbox.controller;

import com.thestars.chatbox.dao.NotificationDAO;
import com.thestars.chatbox.service.MessageService;
import com.thestars.chatbox.service.UserService;
import org.junit.jupiter.api.Test;
import java.security.Principal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NotificationControllerTest {
    UserService users = mock(UserService.class);
    NotificationController controller = new NotificationController(mock(NotificationDAO.class), mock(MessageService.class), users);
    Principal principal = () -> "missing@example.com";

    @Test void listRecentReturnsUnauthorizedWhenUserMissing() {
        when(users.findByEmail(principal.getName())).thenReturn(Optional.empty());
        assertEquals(401, controller.listRecent(principal).getStatusCode().value());
    }

    @Test void unreadCountReturnsUnauthorizedWhenUserMissing() {
        when(users.findByEmail(principal.getName())).thenReturn(Optional.empty());
        assertEquals(401, controller.unreadCount(principal).getStatusCode().value());
    }
}
