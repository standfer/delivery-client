package com.example.hello.maps1.helpers;

import org.joda.time.DateTime;

/**
 * Created by Ivan on 08.01.2019.
 */

public class DateTimeHelper {

    public static boolean isTimeoutPassed(DateTime dateTime, Long timeOut) {
        if (dateTime == null || timeOut == null) return false;

        return dateTime.plus(timeOut).isBeforeNow();
    }
}
