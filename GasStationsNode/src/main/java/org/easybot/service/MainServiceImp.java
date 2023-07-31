package org.easybot.service;

import org.easybot.entity.*;
import org.easybot.enums.GasStationTitle;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MainServiceImp implements MainService{

    private final TelegramAnswer telegramAnswer;
    private final ProduceService produceService;
    private final CommonStationService<CommonStation> commonStationService;
    private final GasStationService gasStationService;

    public MainServiceImp(TelegramAnswer telegramAnswer, ProduceService produceService, CommonStationService<CommonStation> commonStationService, GasStationService gasStationService)
    {
        this.telegramAnswer = telegramAnswer;
        this.produceService = produceService;
        this.commonStationService = commonStationService;
        this.gasStationService = gasStationService;
    }

    @Override
    public void processTextMessage(Update update, String command)
    {
        Optional<GasStationTitle> cd = Arrays.stream(GasStationTitle.values()).filter(x->x.getCommand().equalsIgnoreCase(command)).findFirst();
        cd.ifPresentOrElse(gasStationTitle -> telegramAnswer.formatTextFromObject(retrieveGasStationInfo(gasStationTitle)),
                ()-> telegramAnswer.setText(String.format("Command %s *NOT FOUND*. Please try another command", command)));
        telegramAnswer.setChatId(update.getMessage().getChatId().toString());
        produceService.produceSimpleAnswer(telegramAnswer.mapToSendMessage());


    }

    public List<GasStation> retrieveGasStationInfo(GasStationTitle commands)
    {
        List<GasStation> gasStationList = new ArrayList<>();
        String url = commands.getUrl();
        String gasStationTitle = commands.name().toLowerCase();
        try
        {

            Document document = Jsoup.connect(url).get();
            Elements element = document.select("table > tbody > tr > td");
            List<String> list = modifyListIfNeeded(gasStationTitle, element);
            for (int i = 0; i< list.size()-3;)
            {

                String gasType = list.get(i++);
                String price = list.get(i++);
                String location = list.get(i++);

                CommonStation station = createInstance(gasStationTitle);
                station.setGasType(gasType);
                station.setPrice(price);
                station.setLocation(location);
                station.setGasStationsBrands(gasStationService.findById(commands.getId()));
                commonStationService.save(station);

                gasStationList.add(new GasStation(gasType, price, location));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return gasStationList;
    }


    private CommonStation createInstance(String title)
    {
        switch (title)
        {
            case "neste":
                return new Neste();
            case "circle":
                return new CircleK();
            case "viada":
                return new Viada();
            default:
                throw new RuntimeException("Can't create instance of gas station");
        }
    }


    private List<String> modifyListIfNeeded(String gasStation, Elements elements)
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
        return list.stream().map(x->x.replace("EUR", "")).toList();
    }

}
