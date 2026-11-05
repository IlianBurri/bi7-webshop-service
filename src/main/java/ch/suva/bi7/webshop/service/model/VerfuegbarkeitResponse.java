package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class VerfuegbarkeitResponse {
    public final boolean allesVerfuegbar;
    public final List<VerfuegbarkeitsProblemDto> probleme;

    public VerfuegbarkeitResponse(
            @JsonProperty("probleme") List<VerfuegbarkeitsProblemDto> probleme) {
        this.probleme = probleme == null ? List.of() : List.copyOf(probleme);
        this.allesVerfuegbar = this.probleme.isEmpty();
    }
}
