package com.tcmanna.TCsPitTools.inGameEvent;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PitEventManager {

    public List<EventData> eventList = new ArrayList<>();

    public PitEventManager() {
        getEventJson();
    }

    public void getEventJson() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                String rawTriggers = IOUtils.toString(new URL("https://gitee.com/tcmanna/brookeafk-api/raw/main/events.js"), StandardCharsets.UTF_8);
                Gson gson = new Gson();
                EventData[] data = gson.fromJson(rawTriggers, EventData[].class);

                long currentTimestamp = System.currentTimeMillis();

                //过滤掉那些timestamp大于当前时间戳的数据并排序
                eventList = Arrays.asList(data);
                eventList = filterByTimestampAndSort(eventList, currentTimestamp);

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static List<EventData> filterByTimestampAndSort(List<EventData> dataList, long timestamp) {
        List<EventData> filteredList = new ArrayList<>();
        for (EventData eventData : dataList) {
            if (eventData.getTimestamp() >= timestamp) {
                filteredList.add(eventData);
            }
        }
        filteredList.sort(Comparator.comparing(EventData::getTimestamp));
        return filteredList;
    }

    public List<EventData> getEventList() {
        return eventList;
    }
}
