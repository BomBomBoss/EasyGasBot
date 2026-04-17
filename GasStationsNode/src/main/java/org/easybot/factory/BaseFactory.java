package org.easybot.factory;

import static org.easybot.CommonTexts.CIRCLE_WITHOUT_K_TITLE;
import static org.easybot.CommonTexts.NESTE_TITLE;
import static org.easybot.CommonTexts.VIADA_TITLE;
import static org.easybot.CommonTexts.VIRSI_TITLE;
import org.easybot.entity.history.BaseHistory;
import org.easybot.entity.history.CircleHistory;
import org.easybot.entity.history.NesteHistory;
import org.easybot.entity.history.ViadaHistory;
import org.easybot.entity.history.VirsiHistory;
import org.easybot.entity.stations.BaseStation;
import org.easybot.entity.stations.CircleK;
import org.easybot.entity.stations.Neste;
import org.easybot.entity.stations.Viada;
import org.easybot.entity.stations.Virsi;
import org.easybot.enums.GasStations;
import org.springframework.stereotype.Service;

@Service
public class BaseFactory {

    public BaseStation createStationInstance(final String title) {
        return switch (title) {
            case NESTE_TITLE ->  new Neste();
            case CIRCLE_WITHOUT_K_TITLE -> new CircleK();
            case VIADA_TITLE -> new Viada();
            case VIRSI_TITLE -> new Virsi();
            default -> throw new RuntimeException("Can't create instance of gas station");
        };
    }

    public BaseHistory createHistoryInstance(final GasStations title) {
        return switch (title) {
            case NESTE ->  new NesteHistory();
            case CIRCLE -> new CircleHistory();
            case VIADA -> new ViadaHistory();
            case VIRSI -> new VirsiHistory();
        };
    }
}
