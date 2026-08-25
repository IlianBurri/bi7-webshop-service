package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrentUserResponse {
    public final String email;
    public final boolean isAdmin;

    public CurrentUserResponse(
            @JsonProperty("email") String email,
            @JsonProperty("isAdmin") boolean isAdmin) {
        if (email == null) {
            throw new IllegalArgumentException("email must not be null");
        }
        this.email = email;
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "CurrentUserResponse{" +
                "email='" + email + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
