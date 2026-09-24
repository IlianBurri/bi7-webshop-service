package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckoutRequest {
    public final Integer adressId;

    public CheckoutRequest(@JsonProperty("adressId") Integer adressId) {
        if (adressId == null) {
            throw new IllegalArgumentException("adressId muss eine Zahl sein.");
        }
        this.adressId = adressId;
    }
}
