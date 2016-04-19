package com.xmliu.itravel.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Date: 2016/1/25-16-16:55
 * Email: diyangxia@163.com
 * Author: diyangxia
 * Description: TODO
 */
public class DateUtils {

    private static String TAG = DateUtils.class.getSimpleName();
    /**
     * 起始年月日yyyy-MM-dd与终止年月日yyyy-MM-dd之间的比较。
     *
     * @param dateStr
     *            格式为yyyy-MM-dd
     * @return isFirstBig。
     */
    @SuppressLint("SimpleDateFormat")
    public static String CompareWithNow(String dateStr) {
        String resultDate = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currDateStr = df.format(System.currentTimeMillis());
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date currDate = df.parse(currDateStr);// now
//            LogUtil.i(TAG,"当前时间为" + currDateStr);
            Date cDate = df.parse(dateStr);
            if (currDate.compareTo(cDate) == 1) {
//                LogUtil.i(TAG,"当前时间在后");
                Date currDate2 = df2.parse(currDateStr);
                Date cDate2 = df2.parse(dateStr);
                int result1 = currDate2.compareTo(cDate2);
                if(result1 == 0){
                    // 相等
                    resultDate = dateStr.substring(11,16);
                    // 5分钟前等
                }else if(result1 == -1){
                    // 当前日期小
//                    LogUtil.i(TAG,"当前时间在后-1");
                }else if(result1 == 1){
                    // 当前日期大
//                    LogUtil.i(TAG,"当前时间在后1");
                    if(getIntervalDays(dateStr.substring(0,10),currDateStr.substring(0,10)) == 1){
                        resultDate = "昨天 " + dateStr.substring(11,16);
                    }else{
                        resultDate = dateStr.substring(5,10).replace("-","月") + "日";
                    }
                }
            } else if (currDate.getTime() < cDate.getTime()) {
//                LogUtil.i(TAG,"当前时间在前1");
                resultDate = "当前时间在前";
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return resultDate;
    }


    /**
     * 起始年月日yyyy-MM-dd与终止年月日yyyy-MM-dd之间的比较。
     *
     * @param DATE1
     *            格式为yyyy-MM-dd
     * @param DATE2
     *            格式为yyyy-MM-dd
     * @return isFirstBig。
     */
    @SuppressLint("SimpleDateFormat")
    public static boolean CompareDate(String DATE1, String DATE2) {
        boolean isFirstBig = false;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.compareTo(dt2) == 1) {
                System.out.println("dt1 在dt2前");
                isFirstBig = true;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2后");
                isFirstBig = false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return isFirstBig;
    }
    /**
     * 起始年月yyyy-MM与终止月yyyy-MM之间的比较。
     *
     * @param DATE1
     *            格式为yyyy-MM
     * @param DATE2
     *            格式为yyyy-MM
     * @return isFirstBig。
     */
    @SuppressLint("SimpleDateFormat")
    public static boolean CompareDateMonth(String DATE1, String DATE2) {
        boolean isFirstBig = false;
        DateFormat df = new SimpleDateFormat("yyyy-MM");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.compareTo(dt2) == 1) {
                System.out.println("dt1 在dt2前");
                isFirstBig = true;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2后");
                isFirstBig = false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return isFirstBig;
    }

    /**
     * 起始年月yyyy-MM与终止月yyyy-MM之间跨度的月数。
     *
     * @param beginMonth
     *            格式为yyyy-MM
     * @param endMonth
     *            格式为yyyy-MM
     * @return 整数。
     */
    public static int getIntervalMonth(String beginMonth, String endMonth) {
        int intBeginYear = Integer.parseInt(beginMonth.substring(0, 4));
        int intBeginMonth = Integer.parseInt(beginMonth.substring(beginMonth
                .indexOf("-") + 1));
        int intEndYear = Integer.parseInt(endMonth.substring(0, 4));
        int intEndMonth = Integer.parseInt(endMonth.substring(endMonth
                .indexOf("-") + 1));

        return ((intEndYear - intBeginYear) * 12)
                + (intEndMonth - intBeginMonth) + 1;
    }
    /**
     * 求两个日期相差天数
     *
     * @param sd
     *            起始日期，格式yyyy-MM-dd
     * @param ed
     *            终止日期，格式yyyy-MM-dd
     * @return 两个日期相差天数
     */
    public static long getIntervalDays(String sd, String ed) {
        return ((java.sql.Date.valueOf(ed)).getTime() - (java.sql.Date
                .valueOf(sd)).getTime())
                / (3600 * 24 * 1000);
    }

    /**
     * 得到指定月的天数
     * */
    public static int getMonthLastDay(int year, int month)
    {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 获得指定日期的前十五天
     *
     * @param specifiedDay
     * @return
     */
    public static String getSpecifiedDayFifteen(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 14);

        String dayAfter = new SimpleDateFormat("yyyy-MM-dd")
                .format(c.getTime());
        return dayAfter;
    }
}
