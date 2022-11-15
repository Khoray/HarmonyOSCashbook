package com.khoray.cashbook.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeUtil {
    public static String formatYMD(int year, int month, int day) {
        return "" + Integer.toString(year) + "年" + Integer.toString(month) + "月" + Integer.toString(day) + "日";
    }
    public static String formatYMDHM(int year, int month, int day, int hour, int minute) {
        return "" + Integer.toString(year) + "年" + Integer.toString(month) + "月" + Integer.toString(day) + "日" + Integer.toString(hour) + ":"  + Integer.toString(minute);
    }
    public static long YMDtoTime(int year, int month, int day) {
        DateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
        try {
            return sdf.parse(formatYMD(year, month, day) + "00:00").getTime();
        } catch(Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public static String formatYMDHM(long time) {
        DateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
        return sdf.format(new Date(time));
    }
    public static String formatYMD(long time) {
        DateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(new Date(time));
    }
    public static long YMDHMtoTime(int year, int month, int day, int hour, int minute) {
        DateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
        try {
            return sdf.parse(formatYMDHM(year, month, day, hour, minute)).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
