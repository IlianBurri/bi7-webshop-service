package ch.suva.bi7.webshop.service.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginUserRequestTest {

    @Test
    void erzeugtObjektBeiGültigenDaten() {
        LoginUserRequest request = new LoginUserRequest("test@example.com", "secret123");

        assertEquals("test@example.com", request.email);
        assertEquals("secret123", request.password);
    }

    @Test
    void wirftExceptionWennEmailNullIst() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LoginUserRequest(null, "secret123");
        });
    }

    @Test
    void wirftExceptionWennPasswortNullIst() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LoginUserRequest("test@example.com", null);
        });
    }

    @Test
    void wirftExceptionWennBeideNullSind() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LoginUserRequest(null, null);
        });
    }
}