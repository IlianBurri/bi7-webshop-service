package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginUserResponse {
    public final String status;
    public final String error;

    public LoginUserResponse(
            @JsonProperty("status") String status,
            @JsonProperty("error") String error) {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        this.status = status;
        this.error = error;
    }
}
