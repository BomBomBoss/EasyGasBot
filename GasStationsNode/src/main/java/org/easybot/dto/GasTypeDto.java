package org.easybot.dto;

import lombok.Builder;

@Builder
public record GasTypeDto(String type, String price, String address) {
}
