package ch.suva.bi7.webshop.service.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginUserResponseTest {

    @Test
    void LoginUserResponseHappyCase() {
        LoginUserResponse testee = new LoginUserResponse("SUCCESS", null, "Max", true);

        assertEquals("SUCCESS", testee.status);
        assertNull(testee.error);
        assertEquals("Max", testee.username);
        assertTrue(testee.isAdmin);
    }

    @Test
    void LoginUserResponseOhneIsAdminLiefertFalse() {
        LoginUserResponse testee = new LoginUserResponse("SUCCESS", null, "Max", false);

        assertFalse(testee.isAdmin);
    }

    @Test
    void LoginUserResponseUnHappyCase() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new LoginUserResponse(null, "FAILED", null, false);
        });
        assertEquals("status must not be null", exception.getMessage());
    }
}