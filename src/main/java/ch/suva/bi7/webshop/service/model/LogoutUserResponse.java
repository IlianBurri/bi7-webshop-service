package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LogoutUserResponse {
    public final String status;
    public final String info;

    public LogoutUserResponse(
            @JsonProperty("status") String status,
            @JsonProperty("info") String info) {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        this.status = status;
        this.info = info;
    }
}
