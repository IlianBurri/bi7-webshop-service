package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VerfuegbarkeitsProblemDto {
    public final Integer warenkorbItemId;
    public final Integer artikelId;

    public VerfuegbarkeitsProblemDto(
            @JsonProperty("warenkorbItemId") Integer warenkorbItemId,
            @JsonProperty("artikelId") Integer artikelId) {
        this.warenkorbItemId = warenkorbItemId;
        this.artikelId = artikelId;
    }
}
