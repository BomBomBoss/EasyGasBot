package org.easybot.service;

import lombok.extern.slf4j.Slf4j;
import org.easybot.entity.*;
import org.easybot.enums.GasStationTitle;
import org.easybot.repository.CircleRepository;
import org.easybot.repository.GasStationsRepository;
import org.easybot.repository.NesteRepository;
import org.easybot.repository.ViadaRepository;
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

import static org.easybot.CommonTexts.VIRSI_ALL_STATIONS;

@Service
@Slf4j
public class GasStationService {

    private final GasStationsRepository gasStationsRepository;
    private final CommonStationService  commonStationService;

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
            log.info("Truncating table {}",title.getTitle());
            commonStationService.deleteTable(gasStationTitle);

            try
            {
                Document document = Jsoup.connect(url).get();
                Elements element = parsingWebSites(title, document);
                log.info("pulling gas prices for {}", gasStationTitle);
                Iterator<String> cleanedList = modifyList(gasStationTitle, element);
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
            } catch (IOException e)
            {
                throw new RuntimeException("Occurs problem during prices download from WEB");
            }

        }

    }

    private Elements parsingWebSites(GasStationTitle gasStationTitle, Document document)
    {
        Elements element;

        if (gasStationTitle.getTitle().equals("virsi"))
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
                    case "neste" ->  new Neste();
                    case "circle" -> new CircleK();
                    case "viada" -> new Viada();
                    case "virsi" -> new Virsi();
                    default -> throw new RuntimeException("Can't create instance of gas station");
                };
    }


    private Iterator<String> modifyList(String gasStation, Elements elements)
    {
        List<String> list = elements.stream().map(Element::text).collect(Collectors.toList());

        if (gasStation.equals("neste"))
        {
            list =  list.subList(3, list.size());
        }
        if (gasStation.equals("viada"))
        {
            list.set(0, "95 multi green");
            list.set(3,"95 multi red");
            list.set(6, "98 multi");
            list.set(9, "Diesel");
            list.set(12, "Diesel multi");
            list.set(15, "Gas");
            list.set(18, "E 85");
        }
        if (gasStation.equals("virsi"))
        {
            String rawString = list.get(0);
            list.clear();

            String [] withoutZipCode = rawString
                    .replaceAll("(LV-)[0-9]{4}", "")
                    .replace("Degvielas cenas", "")
                    .replace(VIRSI_ALL_STATIONS, VIRSI_ALL_STATIONS.concat(","))
                    .split(",");
            List<String> listWithoutSpaces = Arrays.stream(withoutZipCode).map(String::trim).toList();
            Iterator<String> iterator = listWithoutSpaces.listIterator();
            while (iterator.hasNext())
            {
                String st = iterator.next();

                if (st.matches(".*\\s.*"))
                {
                    String [] s = st.split(" ", 3);
                    Queue<String> queue = new LinkedList<>(Arrays.asList(s));
                    while (!queue.isEmpty())
                    {
                        list.add(queue.poll());
                    }
                }
                else
                {
                    int lastIndex = list.size() - 1;
                    list.set(lastIndex, list.get(lastIndex).concat(" " + st));
                }
            }

        }
        checkForEmptyFields(list);
        return list.stream().map(x->x.replace("EUR", "")).iterator();
    }

    private void checkForEmptyFields(List<String> list)
    {
        list.removeIf(x -> x == null || x.isEmpty());
    }
}
