package com.napier.sem;

import hello.Greeting;
import hello.GreetingController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GreetingControllerIntegrationTest {

    private GreetingController controller;

    @BeforeEach
    void resetController() {
        controller = new GreetingController();
    }

    @Test
    void testDefaultGreeting() {
        Greeting greeting = controller.greeting("World");
        assertNotNull(greeting);
        assertEquals(0, greeting.getId());
        assertEquals("Hello, World!", greeting.getContent());
    }

    @Test
    void testCustomGreeting() {
        Greeting greeting = controller.greeting("Kevin");
        assertNotNull(greeting);
        assertEquals(0, greeting.getId()); // counter is reset per test
        assertEquals("Hello, Kevin!", greeting.getContent());
    }
}
