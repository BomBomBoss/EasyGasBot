package org.easybot.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
public enum GasStations {
    // Neste stopped publishing fuel prices on their site; frozen until they resume (do not scrape/show/notify).
    NESTE(1,
            "/neste",
            "https://www.neste.lv/lv/content/degvielas-cenas",
            "neste",
            "table > tbody > tr > td",
            false),

    CIRCLE(2,
            "/circle_k",
            "https://www.circlek.lv/privātpersonām/degvielas-cenas",
            "circle_k",
            "table > tbody > tr > td",
            true),

    VIADA(3,
            "/viada",
            "https://www.viada.lv/zemakas-degvielas-cenas/",
            "viada",
            "table > tbody > tr > td",
            true),

    VIRSI(4,
            "/virsi",
            "https://www.virsi.lv/lv/privatpersonam/degviela/degvielas-un-elektrouzlades-cenas",
            "virsi",
            "div.prices-block.fuel-block",
            true);

//    STRAUJUPITE(4,
//                  "/straujupite",
//                  "https://straujupite.lv/degvielas-cenas/",
//                  "straujupite",
//                  "section.straujupite-prices");


    private final String command;
    private final String url;
    private final String title;
    private final String cssQuery;
    private final String buttonId;
    private final long id;
    private final boolean active;


    GasStations(final long id, final String command, final String url, final String title, final String cssQuery, final boolean active) {
        this.id = id;
        this.command = command;
        this.url = url;
        this.title = title;
        this.cssQuery = cssQuery;
        this.buttonId = title.concat("_BUTTON");
        this.active = active;
    }


    public static List<GasStations> getGasStationValues() {
        return Arrays.stream(GasStations.values())
                .filter(GasStations::isActive)
                .toList();
    }
    public static List<String> getGasStationButtonId()
    {
        return getGasStationValues()
                .stream()
                .map(GasStations::getButtonId)
                .toList();
    }

    public static Optional<String> getCommandByButtonId(String buttonId)
    {
        return getGasStationValues()
                .stream()
                .filter(station -> station.buttonId.equals(buttonId))
                .map(GasStations::getCommand)
                .findFirst();
    }


    public static String gasStationTitle(final long id) {
        return Arrays.stream(values()).filter(station -> station.id == id).map(GasStations::getTitle).map(title -> title.replace("_", " ")).findFirst().orElseThrow();
    }

}
