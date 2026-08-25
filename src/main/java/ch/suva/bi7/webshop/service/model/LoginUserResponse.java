package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginUserResponse {
    public final String status;
    public final String error;
    public final String username;
    public final boolean isAdmin;

    public LoginUserResponse(
            @JsonProperty("status") String status,
            @JsonProperty("error") String error,
            @JsonProperty("username") String username,
            @JsonProperty("isAdmin") boolean isAdmin) {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        this.status = status;
        this.error = error;
        this.username = username;
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "LoginUserResponse{" +
                "status='" + status + '\'' +
                ", error='" + error + '\'' +
                ", username='" + username + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
