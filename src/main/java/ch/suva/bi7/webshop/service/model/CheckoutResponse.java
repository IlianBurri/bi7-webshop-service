package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class CheckoutResponse {
    public final Integer bestellungId;
    public final BigDecimal gesamtpreis;
    public final String status;

    public CheckoutResponse(
            @JsonProperty("bestellungId") Integer bestellungId,
            @JsonProperty("gesamtpreis") BigDecimal gesamtpreis,
            @JsonProperty("status") String status) {
        this.bestellungId = bestellungId;
        this.gesamtpreis = gesamtpreis;
        this.status = status;
    }
}
