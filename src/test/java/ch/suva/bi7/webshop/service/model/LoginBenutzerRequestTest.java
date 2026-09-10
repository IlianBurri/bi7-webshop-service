package ch.suva.bi7.webshop.service.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginBenutzerRequestTest {
    @Test
    void erzeugtObjektBeiGültigenDaten() {
        LoginBenutzerRequest request = new LoginBenutzerRequest("test@example.com", "secret123");

        assertEquals("test@example.com", request.email);
        assertEquals("secret123", request.password);
    }

    @Test
    void wirftExceptionWennEmailNullIst() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LoginBenutzerRequest(null, "secret123");
        });
    }

    @Test
    void wirftExceptionWennPasswortNullIst() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LoginBenutzerRequest("test@example.com", null);
        });
    }

    @Test
    void wirftExceptionWennBeideNullSind() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LoginBenutzerRequest(null, null);
        });
    }
}
