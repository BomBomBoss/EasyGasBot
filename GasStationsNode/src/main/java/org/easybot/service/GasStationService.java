package org.easybot.service;

import lombok.extern.slf4j.Slf4j;
import org.easybot.dto.Error;
import org.easybot.entity.CircleK;
import org.easybot.entity.CommonStation;
import org.easybot.entity.GasStationsBrands;
import org.easybot.entity.Neste;
import org.easybot.entity.Viada;
import org.easybot.entity.Virsi;
import org.easybot.enums.GasStationTitle;
import org.easybot.exceptions.ParsingException;
import org.easybot.repository.GasStationsRepository;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.easybot.CommonTexts.CIRCLE_WITHOUT_K_TITLE;
import static org.easybot.CommonTexts.NESTE_TITLE;
import static org.easybot.CommonTexts.VIADA_TITLE;
import static org.easybot.CommonTexts.VIRSI_TITLE;

@Service
@Slf4j
public class GasStationService {

    private final GasStationsRepository gasStationsRepository;
    private final CommonStationService  commonStationService;
    private final ModifierFactory modifierFactory;
    private final List<Error> errors = new ArrayList<>();
    private final ErrorProvider errorProvider;
    private List<CommonStation> rawListOfStations = new ArrayList<>();

    @Autowired
    public GasStationService(GasStationsRepository gasStationsRepository, CommonStationService commonStationService, ModifierFactory modifierFactory, ErrorProvider errorProvider)
    {
        this.gasStationsRepository = gasStationsRepository;
        this.commonStationService = commonStationService;
        this.modifierFactory = modifierFactory;
        this.errorProvider = errorProvider;
    }

    public List<GasStationsBrands> findAllBrands()
    {
        return gasStationsRepository.findAll();
    }
    public GasStationsBrands findById(Long id)
    {
        return gasStationsRepository.findById(id).orElseThrow(() -> new RuntimeException("Can't find this {" + id + "} in table"));
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    public void updateTableWithLatestGasPrices()
    {
        Iterator<GasStationTitle> iterable = GasStationTitle.getGasStationValues().iterator();
        GasStationTitle title;

        while (iterable.hasNext())
        {
            title = iterable.next();
            String url = title.getUrl();
            String gasStationTitle = title.name().toLowerCase();
            Modifier stationModifier = modifierFactory.createModifier(gasStationTitle);

            try
            {
                Document document = Jsoup.connect(url).get();
                Elements element = parsingWebSites(title, document);
                log.info("pulling gas prices for {}", gasStationTitle);

                Iterator<String> cleanedList = modifyList(element, stationModifier, gasStationTitle);
                log.info("Truncating table {}", title.getTitle());
                commonStationService.deleteTable(gasStationTitle);

                while (cleanedList.hasNext())
                {
                    CommonStation station = createInstance(gasStationTitle);
                    String gasType = cleanedList.next();
                    String price = cleanedList.next();
                    String location = cleanedList.next();

                    station.setGasType(stationModifier.adjustCorrectFieldTitleForDB(gasType));
                    station.setPrice(price);
                    station.setLocation(location);
                    station.setGasStationsBrands(findById(title.getId()));

                    validatePulledData(station, gasStationTitle);
                }
                rawListOfStations.clear();
            } catch (IOException | ParsingException | NoSuchElementException e)
            {
                errors.add(new Error(e));
            }
        }
        if (!errors.isEmpty())
        {
            errorProvider.printReport(errors);
        }
    }

    private void validatePulledData(CommonStation station, String gasStationTitle)
    {
        if (station.getPrice().trim().matches("\\d.\\d{3}") && !rawListOfStations.contains(station))
        {
            rawListOfStations.add(station);
            commonStationService.save(station, gasStationTitle);
            log.info("Gas Type - {} was saved to DB", station.getGasType());
        }
    }

    private Elements parsingWebSites(GasStationTitle gasStationTitle, Document document) throws ParsingException
    {
        Elements element = document.select(gasStationTitle.getCssQuery());

        if (element.isEmpty())
        {
            String error = String.format("No data were found under cssQuery %s for gas station: %s", gasStationTitle.getCssQuery(), gasStationTitle.getTitle());
            throw new ParsingException(error);
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


    private Iterator<String> modifyList(Elements elements, Modifier stationModifier, String gasStation) throws ParsingException
    {
        List<String> list = elements.stream().map(Element::text).collect(Collectors.toList());

        list.stream().findAny().orElseThrow(()-> new ParsingException("Jsoup elements are empty for " + gasStation));

        list = stationModifier.cleanRawElements(list);
        checkForEmptyFields(list);
        return list.stream().map(x -> x.replace("EUR", "")).iterator();
    }

    private void checkForEmptyFields(List<String> list)
    {
        list.removeIf(x -> x == null || x.isEmpty());
    }

    public ModifierFactory getModifierFactory()
    {
        return modifierFactory;
    }

    public List<Error> getErrorReports() {
        return errors;
    }
}
