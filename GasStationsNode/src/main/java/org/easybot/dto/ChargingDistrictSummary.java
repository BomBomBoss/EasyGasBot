package org.easybot.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ChargingDistrictSummary(String district, String stationName, String address,
                                       String connectorTypeLabel, BigDecimal powerKw, BigDecimal pricePerKwh) {
}
