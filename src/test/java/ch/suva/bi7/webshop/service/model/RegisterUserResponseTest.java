package ch.suva.bi7.webshop.service.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterUserResponseTest {
    @Test
    void RegisterUserResponseHappyCase() {

        RegisterUserResponse testee = new RegisterUserResponse("SUCCESS", null);

        assertEquals("SUCCESS",testee.status);
        assertNull(testee.error);
    }

    @Test
    void registerUserResponseUnHappyCase() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new RegisterUserResponse(null, "FAILED");
        });
        assertEquals("status must not be null", exception.getMessage());
    }
}