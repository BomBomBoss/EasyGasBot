package org.easybot.dto.fuel_types;

import lombok.Builder;
import org.easybot.entity.enums.GasTypesName;

@Builder
public record FuelHistoryData(Long brandId, GasTypesName type, String price) {
}
