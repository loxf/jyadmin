package org.loxf.jyadmin.base.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtils {
    /**
     * 一天中小时数
     */
    private static final String[] DAYS_P_HOUR_CY = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10"
            , "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};
    /**
     * 闰年中每月天数
     */
    private static final int[] DAYS_P_MONTH_LY = {31, 29, 31, 30, 31, 30, 31,
            31, 30, 31, 30, 31};
    /**
     * 非闰年中每月天数
     */
    private static final int[] DAYS_P_MONTH_CY = {31, 28, 31, 30, 31, 30, 31,
            31, 30, 31, 30, 31};
    /**
     * 代表数组里的年、月、日
     */
    private static final int Y = 0, M = 1, D = 2;
    private static Logger log = LoggerFactory.getLogger(DateUtils.class);
    private static transient int gregorianCutoverYear = 1582;
    private DateUtils() {
    }

    /**
     * 获取某天的起始时间, e.g. 2005-10-01 00:00:00.000
     *
     * @param date 日期对象
     * @return 该天的起始时间
     */
    public static Date getStartDate(Date date) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * 获取某天的结束时间, e.g. 2005-10-01 23:59:59.999
     *
     * @param date 日期对象
     * @return 该天的结束时间
     */
    public static Date getEndDate(Date date) {

        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        return cal.getTime();
    }

    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    public static String format(Date time) {
        if (time == null) {
            return "";
        }
        return getFormat().format(time);
    }

    public static String format(Date time, String string) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(string);
            return format.format(time);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String formatHms(Date time) {
        if (time == null) {
            return "";
        }
        return getFormatHms().format(time);
    }

    public static Date toDate(String time) {
        if (StringUtils.isEmpty(time)) {
            return null;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.parse(time);
        } catch (ParseException e) {
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Date toDate(String time,String pattern) {
        if (StringUtils.isEmpty(time)) {
            return null;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.parse(time);
        } catch (ParseException e) {
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static SimpleDateFormat getFormat() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    private static SimpleDateFormat getFormatHms() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 天转毫秒
     *
     * @return
     */
    public static Long getDayToTime(long day) {
        return day * 24 * 60 * 60 * 1000;
    }

    /**
     * 将不足四位的年份补足为四位
     *
     * @param decimal
     * @return
     */
    public static String formatYear(int decimal) {
        DecimalFormat df = new DecimalFormat("0000");
        return df.format(decimal);
    }

    /**
     * 将不足两位的月份或日期补足为两位
     *
     * @param decimal
     * @return
     */
    public static String formatMonthDay(int decimal) {
        DecimalFormat df = new DecimalFormat("00");
        return df.format(decimal);
    }

    /**
     * 检查传入的参数代表的年份是否为闰年
     *
     * @param year
     * @return
     */
    public static boolean isLeapYear(int year) {
        return year >= gregorianCutoverYear ? ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0)))
                : (year % 4 == 0);
    }

    /**
     * 日期加1天
     *
     * @param year1
     * @param month1
     * @param day1
     * @return
     */
    private static int[] addOneDay(int year1, int month1, int day1) {
        int year = year1;
        int month = month1;
        int day = day1;
        if (isLeapYear(year)) {
            day++;
            if (day > DAYS_P_MONTH_LY[month - 1]) {
                month++;
                if (month > 12) {
                    year++;
                    month = 1;
                }
                day = 1;
            }
        } else {
            day++;
            if (day > DAYS_P_MONTH_CY[month - 1]) {
                month++;
                if (month > 12) {
                    year++;
                    month = 1;
                }
                day = 1;
            }
        }
        int[] ymd = {year, month, day};
        return ymd;
    }

    /**
     * 将代表日期的字符串分割为代表年月日的整形数组
     *
     * @param date1
     * @return
     */
    public static int[] splitYMD(String date1) {
        String date = date1;
        date = date.replace("-", "");
        int[] ymd = {0, 0, 0};
        ymd[Y] = Integer.parseInt(date.substring(0, 4));
        ymd[M] = Integer.parseInt(date.substring(4, 6));
        ymd[D] = Integer.parseInt(date.substring(6, 8));
        return ymd;
    }

    /**
     * 计算两个日期之间相隔的天数
     *
     * @param begin
     * @param end
     * @return
     * @throws ParseException
     */
    public static long countDay(String begin, String end) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate, endDate;
        long day = 0;
        try {
            beginDate = format.parse(begin);
            endDate = format.parse(end);
            day = (endDate.getTime() - beginDate.getTime())
                    / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            log.error("日期解析失败:", e);
        }
        return day;
    }

    /**
     * 以循环的方式计算日期
     *
     * @param beginDate endDate
     * @param
     * @return
     * @throws ParseException
     */
    public static List<Date> getEveryday(String beginDate, String endDate)
            throws ParseException {
        long days = countDay(beginDate, endDate);
        int[] ymd = splitYMD(beginDate);
        List<String> everyDays = new ArrayList<String>();
        everyDays.add(beginDate);
        for (int i = 0; i < days; i++) {
            ymd = addOneDay(ymd[Y], ymd[M], ymd[D]);
            everyDays.add(formatYear(ymd[Y]) + "-" + formatMonthDay(ymd[M])
                    + "-" + formatMonthDay(ymd[D]));
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<Date> dates = new ArrayList<Date>();
        for (String s : everyDays) {
            Date d = format.parse(s);
            dates.add(d);
        }
        return dates;
    }

    /**
     * 获取指定日期减去指定时间
     * @param date
     * @param field
     * @param month1
     * @return
     */
    public static Date getSubtractTime(Date date, int field, int month1) {
        int month = month1;
        if (month == 0) {
            month = 1;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, -month);// 得到前一个月
        return calendar.getTime();
    }

    /**
     * 获取指定日期加上指定时间
     *
     * @param month
     * @param field 同Calendar.${field}
     * @return
     */
    public static Date getAddTime(Date date, int field, int month) {
        return getSubtractTime(date, field, -month);
    }
}
