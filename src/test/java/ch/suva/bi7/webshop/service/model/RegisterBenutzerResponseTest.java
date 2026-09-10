package ch.suva.bi7.webshop.service.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterBenutzerResponseTest {
    @Test
    void RegisterBenutzerResponseHappyCase() {

        RegisterBenutzerResponse testee = new RegisterBenutzerResponse("SUCCESS", null);

        assertEquals("SUCCESS",testee.status);
        assertNull(testee.error);
    }

    @Test
    void registerBenutzerAntwortFehlerfall() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new RegisterBenutzerResponse(null, "FAILED");
        });
        assertEquals("status must not be null", exception.getMessage());
    }
}