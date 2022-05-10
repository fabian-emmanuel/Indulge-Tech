package com.indulgetech.utils;


import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.*;

public class CommonUtils {


    public static boolean fieldChanged(String val1, String val2) {
        return !val1.equals(val2);
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(6);
    }

    public static boolean checkDuplicateList(Collection inputList) {

        Set inputSet = new HashSet(inputList);

        if (inputSet.size() < inputList.size()) {
            return true;
        }
        return false;
    }

    public static boolean isLong(String str) {
        try {
            Long.parseLong(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static int round(double d) {
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - i;
        if (result < 0.5) {
            return d < 0 ? -i : i;
        } else {
            return d < 0 ? -(i + 1) : i + 1;
        }
    }

    public static double roundWithPrecision(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    /**
     * generate random number between low and high.
     * @param low
     * @param high
     * @return
     */
    public static int getRandomInt(int low,int high){
        Random r=new Random();
        return r.nextInt((high)-low) + low;
    }
}



