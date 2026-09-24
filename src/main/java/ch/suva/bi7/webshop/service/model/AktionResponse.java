package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AktionResponse {
    public final String status;
    public final String message;

    public AktionResponse(
            @JsonProperty("status") String status,
            @JsonProperty("message") String message) {
        this.status = status;
        this.message = message;
    }
}
