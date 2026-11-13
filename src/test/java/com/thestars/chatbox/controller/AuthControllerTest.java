package com.thestars.chatbox.controller;

import com.thestars.chatbox.model.User;
import com.thestars.chatbox.service.FileService;
import com.thestars.chatbox.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    UserService users = mock(UserService.class);
    PasswordEncoder encoder = mock(PasswordEncoder.class);
    AuthController controller = new AuthController(users, encoder, mock(FileService.class));

    @Test void loginRejectsUnknownEmail() {
        when(users.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        ResponseEntity<?> response = controller.login(Map.of("email", "missing@example.com", "password", "x"));
        assertEquals(401, response.getStatusCode().value());
    }

    @Test void registerRejectsMissingFields() {
        ResponseEntity<?> response = controller.registerUser(Map.of("email", "a@example.com"));
        assertEquals(400, response.getStatusCode().value());
    }
}
