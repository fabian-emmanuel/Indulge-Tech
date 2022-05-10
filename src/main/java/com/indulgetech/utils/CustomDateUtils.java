package com.indulgetech.utils;

import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomDateUtils {
    public static Date now() {
        return Date.from(Instant.now());
    }
    public static Date today() {
        return DateUtils.truncate(now(),Calendar.DATE);
    }
    public static String getShortMonthName(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        final String[] months = new DateFormatSymbols(Locale.ENGLISH).getShortMonths();
        return months[calendar.get(Calendar.MONTH)];
    }

    public static boolean isValidFormat(String format, String value) {
        if(value==null){
            return false;
        }
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }

    public static String getPresentYear() {
        Date dt = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return format.format(new Date(dt.getTime()));
    }
}
