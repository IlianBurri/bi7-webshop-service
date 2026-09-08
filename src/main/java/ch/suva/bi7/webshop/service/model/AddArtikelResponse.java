package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddArtikelResponse {
    private ArtikelDto artikel;

    public AddArtikelResponse(
            @JsonProperty("artikel") ArtikelDto artikel) {
        this.artikel = artikel;
    }

    public ArtikelDto getArtikel() {
        return artikel;
    }
}
