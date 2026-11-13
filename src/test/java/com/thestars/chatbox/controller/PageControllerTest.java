package com.thestars.chatbox.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PageControllerTest {
    private final PageController controller = new PageController();

    @Test void indexReturnsIndexView() { assertEquals("index", controller.index()); }
    @Test void loginReturnsLoginView() { assertEquals("login", controller.login()); }
}
