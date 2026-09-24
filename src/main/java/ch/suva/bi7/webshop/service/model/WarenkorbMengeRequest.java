package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WarenkorbMengeRequest {
    public final Integer menge;

    public WarenkorbMengeRequest(@JsonProperty("menge") Integer menge) {
        if (menge == null || menge < 1) {
            throw new IllegalArgumentException("menge muss mindestens 1 sein.");
        }
        this.menge = menge;
    }
}
