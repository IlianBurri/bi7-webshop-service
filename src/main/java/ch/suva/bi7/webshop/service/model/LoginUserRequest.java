package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginUserRequest {
    public final String email;
    public final String password;

    public LoginUserRequest(
            @JsonProperty("email") String email,
            @JsonProperty("password") String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("email and password must not be null");
        }
        this.email = email;
        this.password = password;
    }
}
