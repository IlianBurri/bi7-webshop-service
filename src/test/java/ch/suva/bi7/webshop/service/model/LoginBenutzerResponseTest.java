package ch.suva.bi7.webshop.service.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginBenutzerResponseTest {

    @Test
    void LoginBenutzerResponseHappyCase() {
        LoginBenutzerResponse testee = new LoginBenutzerResponse("SUCCESS", null, "Max", true);

        assertEquals("SUCCESS", testee.status);
        assertNull(testee.error);
        assertEquals("Max", testee.username);
        assertTrue(testee.isAdmin);
    }

    @Test
    void LoginBenutzerResponseOhneIsAdminLiefertFalse() {
        LoginBenutzerResponse testee = new LoginBenutzerResponse("SUCCESS", null, "Max", false);

        assertFalse(testee.isAdmin);
    }

    @Test
    void LoginBenutzerResponseUnHappyCase() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new LoginBenutzerResponse(null, "FAILED", null, false);
        });
        assertEquals("status must not be null", exception.getMessage());
    }
}