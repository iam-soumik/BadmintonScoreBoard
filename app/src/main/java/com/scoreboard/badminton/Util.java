package com.scoreboard.badminton;

import java.util.concurrent.TimeUnit;

/**
 * Created by HOME on 1/7/2019.
 */

public class Util {

    public static String gettimeDuration(long millis){
        String duration = "";
        duration = String.format("%02d:%02d:%02d",
        TimeUnit.MILLISECONDS.toHours(millis),
        TimeUnit.MILLISECONDS.toMinutes(millis) -
        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
        TimeUnit.MILLISECONDS.toSeconds(millis) -
        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return duration;
    }
}
