package com.lab430.utility;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.lab430.model.Duration;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lab430 on 15/6/21.
 */
public class DurationDeserializer implements JsonDeserializer<Duration>{

    private int[] tempArray = new int[Duration.numVol];

    @Override
    public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Duration duration = new Duration();
        String timeStr = json.getAsString();

        Pattern p = Pattern.compile("(\\d+-)??(\\d+:)??(\\d+:)??(\\d+)");
        Matcher m = p.matcher(timeStr);

        int count = 0;
        while(m.find()) {
            String result = m.group();
            if(result != null && result.length() > 0) {
                tempArray[count] = Integer.parseInt(result);
            }
            else {
                tempArray[count] = 0;
            }
            count++;
        }

        for(int i = count - 1;i >= 0;i--) {
            duration.volume[count - 1 - i] = tempArray[i];
        }

        return duration;
    }
}
