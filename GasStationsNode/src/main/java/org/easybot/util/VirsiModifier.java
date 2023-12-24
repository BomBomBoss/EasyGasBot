package org.easybot.util;

import org.springframework.stereotype.Service;

import java.util.*;

import static org.easybot.CommonTexts.VIRSI_ALL_STATIONS;

@Service
public class VirsiModifier extends GasTypeFormatter{

    public VirsiModifier()
    {
        super("95E", null,
                "98E", null,
                "DD",null,
                "CNG", "LPG", "AdBLUE");
    }

    @Override
    public List<String> cleanRawElements(List<String> rawList)
    {
        String rawString = rawList.get(0);
        rawList.clear();

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

            if (st.matches(".*\\s.*") && !st.contains("pag.") && !st.contains("novads"))
            {
                String [] s = st.split(" ", 3);
                Queue<String> queue = new LinkedList<>(Arrays.asList(s));
                while (!queue.isEmpty())
                {
                    rawList.add(queue.poll());
                }
            }
            else
            {
                int lastIndex = rawList.size() - 1;
                rawList.set(lastIndex, rawList.get(lastIndex).concat(" " + st));
            }
        }

        return rawList;
    }
}
