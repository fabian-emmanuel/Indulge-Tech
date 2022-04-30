package com.indulgetech.utils;

import java.time.Instant;
import java.util.Date;

public class CustomDateUtils {
    public static Date now() {
        return Date.from(Instant.now());
    }

}
