package com.spt.bas.report.client.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.util.StringUtils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

    /**
     * 解析日期字符串为Date对象
     */
    public static Date parseDate(String dateStr) {
        if(StringUtils.isNotBlank(dateStr)){
            return DateUtil.parseDate(dateStr);
        }
        return null;
       
    }
    
    /**
     * 获取当前日期的开始时间
     * @return
     */
    public static Date getDayBegin() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取当前日期的结束时间
     * @return
     */
    public static Date getDayEnd() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * 获取日期后几天的时间
     * @param date
     * @param day
     * @return
     */
    public static Date getDayAfter(Date date,int day){
        Calendar now =Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE,now.get(Calendar.DATE)+day);
        return now.getTime();
    }

    /**
     *  获取过去第n周的开始时间
     */
    public static Date getBeginDayOfLastWeek(int n) {
        Date date = new Date();
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek - 7*n);
        return getDayStartTime(cal.getTime());
    }

    /**
     * 获取过去第n周的结束时间
     */
    public static Date getEndDayOfLastWeek(int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfLastWeek(n));
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }

    /**
     * 获取某个日期的开始时间
     * @param d
     * @return
     */
    public static Timestamp getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d)
            calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
     * 获取某个日期的结束时间
     * @param d
     * @return
     */
    public static Timestamp getDayEndTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d)
            calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }
    /**
     * 获取指定日期是第几周
     */
    public static void a(){}

    /**
     * 根据日期字符串判断当月第几周
     * @param str
     * @return
     * @throws Exception
     */
    public static int getWeek(String str){
        Date date = new Date();
        if (date == null) {
            return -1;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //第几周
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        //第几天，从周日开始
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return week;
    }
    // 获取上月的开始时间
    public static Date getBeginDayOfLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 3, 1);
        return getDayStartTime(calendar.getTime());
    }

    // 获取上月的结束时间
    public static Date getEndDayOfLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 2, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 2, day);
        return getDayEndTime(calendar.getTime());
    }

    // 获取本月的开始时间
    public static Date getBeginDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        // 设置为当月的第一天
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        // 设置时间为零点零分零秒
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * 根据日期获取当月开始时间
     */
    public static Date getBeginDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        // 设置时间为零点零分零秒
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        return calendar.getTime();
    }
    /**
     * 根据日期获取上月开始时间
     */
    public static Date getBeginDayOfLastMonth(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, num);
        return getBeginDayOfMonth(calendar.getTime());
    }
        // 获取本月的结束时间
    public static Date getEndDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        // 将日期设置为当月的最后一天
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        // 设置时间为23:59:59
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    /**
     * 根据日期获取当月结束时间
     * @return
     */
    public static Date getEndDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        // 设置时间为23:59:59
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
    /**
     * 根据日期获取上月结束时间
     */
    public static Date getEndDayOfLastMonth(Date date, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, num);
        return getEndDayOfMonth(calendar.getTime());
    }

    // 获取今年是哪一年
    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(1));
    }

    // 获取本月是哪一月
    public static int getNowMonth() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(2) + 1;
    }
    public static void main(String[] args) {
        Date beginDayOfLastMonth = getBeginDayOfLastMonth();
        Date endDayOfLastMonth = getEndDayOfLastMonth();
        System.out.println(beginDayOfLastMonth);
        System.out.println(endDayOfLastMonth);
        
        Date quarterStartDate = getQuarterStartDate("2026-05");
        Date quarterEndDate = getQuarterEndDate("2026-05");
        System.out.println(quarterStartDate.toString());
        System.out.println(quarterEndDate.toString());

    }

    /**
     * 根据月份如：2026-04 获取所在季度的开始日期 2026-04-01
     */
    public static Date getQuarterStartDate(String month) {
        // 拆分 2026-04
        String[] parts = month.split("-");
        int year = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);

        // 计算季度起始月份
        int startMonth = ((m - 1) / 3) * 3 + 1;

        Calendar cal = Calendar.getInstance();
        cal.clear(); // 很关键，避免时间残留

        // Calendar 月份从 0 开始，所以要 -1
        cal.set(year, startMonth - 1, 1, 0, 0, 0);

        return cal.getTime();
    }
    

    /**
     * 根据月份如：2026-04 获取所在季度的结束日期 2026-06-30
     */
    public static Date getQuarterEndDate(String month) {
        String[] parts = month.split("-");
        int year = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);

        int startMonth = ((m - 1) / 3) * 3 + 1;

        Calendar cal = Calendar.getInstance();
        cal.clear();

        // 下个季度第一天 - 1 秒
        cal.set(year, startMonth - 1 + 3, 1, 0, 0, 0);
        cal.add(Calendar.SECOND, -1);

        return cal.getTime();
    }

}
