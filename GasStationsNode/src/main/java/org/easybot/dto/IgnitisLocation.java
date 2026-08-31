package org.easybot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IgnitisLocation(
        @JsonProperty("id") String id,
        @JsonProperty("label") String label,
        @JsonProperty("address") String address,
        @JsonProperty("status") String status,
        @JsonProperty("is_partner") boolean isPartner,
        @JsonProperty("is_always_open") boolean isAlwaysOpen,
        @JsonProperty("availability") JsonNode availability,
        @JsonProperty("latlng") List<String> latlng,
        @JsonProperty("connectors_grouped") List<IgnitisConnectorGroup> connectorsGrouped) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record IgnitisConnectorGroup(
            @JsonProperty("count") int count,
            @JsonProperty("type") String type,
            @JsonProperty("type_label") String typeLabel,
            @JsonProperty("power") BigDecimal power,
            @JsonProperty("price") BigDecimal price) {
    }

}
