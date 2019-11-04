package com.example.hello.maps1.helpers.data_types;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by Ivan on 08.01.2019.
 */

public class DateTimeHelper {

    public static boolean isTimeoutPassed(DateTime dateTime, Long timeOut) {
        if (dateTime == null || timeOut == null) return false;

        return dateTime.plus(timeOut).isBeforeNow();
    }

    public static String getCurrentDateTimeFormatted() {
        return DateTimeFormat.forPattern("YYYY-MM-dd_HH_mm").print(new DateTime());
    }
}
