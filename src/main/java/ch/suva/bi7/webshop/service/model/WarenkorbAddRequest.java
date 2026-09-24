package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WarenkorbAddRequest {
    public final String email;
    public final Integer artikelId;
    public final Integer menge;

    public WarenkorbAddRequest(
            @JsonProperty("email") String email,
            @JsonProperty("artikelId") Integer artikelId,
            @JsonProperty("menge") Integer menge) {
        if (email == null || email.trim().isEmpty()
                || artikelId == null || menge != null && menge < 1) {
            throw new IllegalArgumentException(
                    "email, artikelId und menge müssen gültig sein.");
        }
        this.email = email;
        this.artikelId = artikelId;
        this.menge = menge == null ? 1 : menge;
    }
}
