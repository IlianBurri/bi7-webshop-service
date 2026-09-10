package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BenutzerDto {

    private final String username;
    private final String email;
    private final boolean isAdmin;

    public BenutzerDto(
            @JsonProperty("username") String username,
            @JsonProperty("email") String email,
            @JsonProperty("isAdmin") boolean isAdmin) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("username darf nicht null/leer sein");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("email darf nicht null/leer sein");
        }

        this.username = username.trim();
        this.email = email.trim();
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
