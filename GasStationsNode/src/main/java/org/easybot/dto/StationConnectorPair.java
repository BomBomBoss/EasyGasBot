package org.easybot.dto;

import org.easybot.entity.charging.ChargingConnector;
import org.easybot.entity.charging.ChargingStation;

public record StationConnectorPair(ChargingStation station, ChargingConnector connector) {
}
