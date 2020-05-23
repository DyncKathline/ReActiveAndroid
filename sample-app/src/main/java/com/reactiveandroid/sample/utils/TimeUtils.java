package com.reactiveandroid.sample.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * TimeUtils
 */
public class TimeUtils {

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yy/MM/dd");
    public static final SimpleDateFormat DF_YYYY_MM_DD_HH_MM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final SimpleDateFormat DF_MM_DD_HH_MM = new SimpleDateFormat("MM-dd HH:mm");
    public static final SimpleDateFormat DF_HH_MM_SS = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat DF_MM_SS = new SimpleDateFormat("mm:ss");

    private TimeUtils() {
        throw new AssertionError();
    }

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @param timeZone     the ID for a <code>TimeZone</code>, either an abbreviation
     *                     such as "PST", a full name such as "America/Los_Angeles", or a custom
     *                     ID such as "GMT-8:00". Note that the support of abbreviations is
     *                     for JDK 1.1.x compatibility only and full names should be used.
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat, TimeZone timeZone) {
        if (timeZone != null) {
            dateFormat.setTimeZone(timeZone);
        }
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        Calendar nowCal = Calendar.getInstance();
        TimeZone localZone = nowCal.getTimeZone();
        return getTime(timeInMillis, dateFormat, localZone);
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    public static long getNowTime() {
        return System.currentTimeMillis();
    }

    public static Date parseDate(String serverTime, SimpleDateFormat dateFormat) {
        if (dateFormat == null) {
            dateFormat =  DEFAULT_DATE_FORMAT;
        }
        SimpleDateFormat sdf = dateFormat;
        Calendar nowCal = Calendar.getInstance();
        TimeZone localZone = nowCal.getTimeZone();
        sdf.setTimeZone(localZone);//TimeZone.getTimeZone("GMT+8:00")
        Date date = null;
        try {
            date = sdf.parse(serverTime);
        } catch (Exception e) {

        }
        return date;
    }

    /**
     * 是否是同一年
     *
     * @param timeInMillis
     * @return
     */
    public static boolean isCurrentYear(long timeInMillis) {
        String year = getTime(timeInMillis, new SimpleDateFormat("yyyy"));
        String curYear = getTime(getCurrentTimeInLong(), new SimpleDateFormat("yyyy"));
        if (year.equals(curYear)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是同一月
     *
     * @param timeInMillis
     * @return
     */
    public static boolean isCurrentMouth(long timeInMillis) {
        String mouth = getTime(timeInMillis, new SimpleDateFormat("MM"));
        String curMouth = getTime(getCurrentTimeInLong(), new SimpleDateFormat("MM"));
        if (mouth.equals(curMouth)) {
            return true;
        }
        return false;
    }

    /**
     * 是否是同一天
     *
     * @param timeInMillis
     * @return
     */
    public static boolean isCurrentDay(long timeInMillis) {
        String day = getTime(timeInMillis, new SimpleDateFormat("dd"));
        String curDay = getTime(getCurrentTimeInLong(), new SimpleDateFormat("dd"));
        if (day.equals(curDay)) {
            return true;
        }
        return false;
    }

    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;

    private static final String ONE_SECOND_AGO = "秒前";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String ONE_HOUR_AGO = "小时前";
    private static final String ONE_DAY_AGO = "天前";
    private static final String ONE_MONTH_AGO = "月前";
    private static final String ONE_YEAR_AGO = "年前";

    /**
     * 转化为几天前这种形式。
     *
     * @param millis
     * @return
     */
    public static String format(long millis) {
        long delta = System.currentTimeMillis() - millis;
        if (millis < System.currentTimeMillis()) {//小于当前时间
            if (delta < 1L * ONE_MINUTE) {
//            long seconds = toSeconds(delta);
//            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
                return 1 + ONE_MINUTE_AGO;
            }
            if (delta < 45L * ONE_MINUTE) {
                long minutes = toMinutes(delta);
                return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
            }
            if (delta < 24L * ONE_HOUR) {
                long hours = toHours(delta);
                return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
            }
            if (delta < 48L * ONE_HOUR) {
                return "昨天";
            }
            if (delta < 30L * ONE_DAY) {
                long days = toDays(delta);
                return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
            }
            if (delta < 12L * 4L * ONE_WEEK) {
                long months = toMonths(delta);
                return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
            } else {
                long years = toYears(delta);
                return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
            }
        } else {//大于当前时间

            return getTime(millis);
        }

    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }

    public static String getWebsiteDatetime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy - MM - dd", Locale.CHINA);// 输出北京时间
        return getCurrentTimeInString(sdf);
    }
}
