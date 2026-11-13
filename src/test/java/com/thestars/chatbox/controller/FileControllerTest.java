package com.thestars.chatbox.controller;

import com.thestars.chatbox.service.FileService;
import com.thestars.chatbox.service.MessageService;
import com.thestars.chatbox.service.UserService;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileControllerTest {
    FileService files = mock(FileService.class);
    FileController controller = new FileController(files, mock(MessageService.class), mock(UserService.class));

    @Test void downloadReturnsNotFoundForUnknownAttachment() {
        when(files.findById(12L)).thenReturn(java.util.Optional.empty());
        assertEquals(404, controller.downloadFile(12L).getStatusCode().value());
    }

    @Test void attachmentsImageFilterReturnsImages() {
        when(files.getConversationImages(4L)).thenReturn(List.of());
        assertEquals(200, controller.getAttachments(4L, "image").getStatusCode().value());
        verify(files).getConversationImages(4L);
        verify(files, never()).getConversationFiles(4L);
    }
}
