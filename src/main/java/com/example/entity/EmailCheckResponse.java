package com.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailCheckResponse {

    @JsonProperty("email_deliverability")
    private Deliverability deliverability;

    @JsonProperty("email_quality")
    private Quality quality;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Deliverability {
        private String status; //"deliverable"
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Quality {
        @JsonProperty("is_disposable")
        private boolean disposable;

        private double score;
    }
}