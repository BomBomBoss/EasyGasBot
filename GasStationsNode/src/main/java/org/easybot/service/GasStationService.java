package org.easybot.service;

import lombok.extern.slf4j.Slf4j;
import org.easybot.entity.*;
import org.easybot.enums.GasStationTitle;
import org.easybot.exceptions.ParsingException;
import org.easybot.repository.CircleRepository;
import org.easybot.repository.GasStationsRepository;
import org.easybot.repository.NesteRepository;
import org.easybot.repository.ViadaRepository;
import org.easybot.util.Modifier;
import org.easybot.util.ModifierFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.easybot.CommonTexts.*;

@Service
@Slf4j
public class GasStationService {

    private final GasStationsRepository gasStationsRepository;
    private final CommonStationService  commonStationService;
    private final EnumMap<GasStationTitle, String> validationReport = new EnumMap<>(GasStationTitle.class);

    @Autowired
    public GasStationService(GasStationsRepository gasStationsRepository, CommonStationService commonStationService, NesteRepository nesteRepository, CircleRepository circleRepository, ViadaRepository viadaRepository)
    {
        this.gasStationsRepository = gasStationsRepository;
        this.commonStationService = commonStationService;
    }

    public List<GasStationsBrands> findAllBrands()
    {
        return gasStationsRepository.findAll();
    }
    public GasStationsBrands findById(Long id)
    {
        return gasStationsRepository.findById(id).orElseThrow(()->new RuntimeException("Can't find this {" + id + "} in table"));
    }

    @Scheduled(initialDelay = 1/60, fixedRate = 1, timeUnit = TimeUnit.HOURS)
    private void updateTableWithLatestGasPrices()
    {
        Iterator<GasStationTitle> iterable = Arrays.stream(GasStationTitle.values()).iterator();
        GasStationTitle title;

        while (iterable.hasNext())
        {
            title = iterable.next();
            String url = title.getUrl();
            String gasStationTitle = title.name().toLowerCase();

            try
            {
                Document document = Jsoup.connect(url).get();
                Elements element = parsingWebSites(title, document);
                log.info("pulling gas prices for {}", gasStationTitle);
                Iterator<String> cleanedList = modifyList(gasStationTitle, element);
                log.info("Truncating table {}", title.getTitle());
                commonStationService.deleteTable(gasStationTitle);
                while (cleanedList.hasNext())
                {
                    String gasType = cleanedList.next();
                    String price = cleanedList.next();
                    String location = cleanedList.next();

                    CommonStation station = createInstance(gasStationTitle);
                    station.setGasType(gasType);
                    station.setPrice(price);
                    station.setLocation(location);
                    station.setGasStationsBrands(findById(title.getId()));
                    commonStationService.save(station, gasStationTitle);
                    log.info("Gas Type - {} was saved to DB", gasType);
                }
            } catch (IOException | ParsingException e)
            {
                validationReport.put(title, e.toString());
            }

        }

        if (!validationReport.isEmpty())
        {
            printErrorReport(validationReport);
        }


    }

    private Elements parsingWebSites(GasStationTitle gasStationTitle, Document document)
    {
        Elements element;

        if (gasStationTitle.getTitle().equals(VIRSI_TITLE))
        {
             element = document.select("div.prices-block.fuel-block");
        }
        else
        {
             element = document.select("table > tbody > tr > td");
        }
        return element;
    }

    private CommonStation createInstance(String title)
    {

        return switch (title)
                {
                    case NESTE_TITLE ->  new Neste();
                    case CIRCLE_WITHOUT_K_TITLE -> new CircleK();
                    case VIADA_TITLE -> new Viada();
                    case VIRSI_TITLE -> new Virsi();
                    default -> throw new RuntimeException("Can't create instance of gas station");
                };
    }


    private Iterator<String> modifyList(String gasStation, Elements elements) throws ParsingException
    {
        List<String> list = elements.stream().map(Element::text).collect(Collectors.toList());

        if (list.isEmpty())
        {
            throw new ParsingException("Jsoup elements are empty");
        }

        Modifier stationModifier = ModifierFactory.createModifier(gasStation);

        list = stationModifier.cleanRawElements(list);
        checkForEmptyFields(list);
        return list.stream().map(x -> x.replace("EUR", "")).iterator();
    }

    private void checkForEmptyFields(List<String> list)
    {
        list.removeIf(x -> x == null || x.isEmpty());
    }

    private void printErrorReport (EnumMap<GasStationTitle, String> errorReport)
    {
        for (Map.Entry<GasStationTitle, String> entry : errorReport.entrySet())
        {
            log.error("Error during gas stations price download from url: " + entry.getKey().getUrl() + ".Reason: " + entry.getValue());
        }
        errorReport.clear();
    }


}
