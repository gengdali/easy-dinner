package com.huizi.easydinner.wechat.util;


import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AppUtil {

    /**
     *
     */
    public static String checkNull(String str) {
        if (null == str)
            str = "";
        return str;
    }


    /**
     * 获取月份最后一天日期
     *
     * @param yyyymm 月份
     * @return 月份最后一天日期
     */
    public static String getMonthLastDay(String yyyymm) {
        int year = Integer.parseInt(yyyymm.substring(0, 4));
        int month = Integer.parseInt(yyyymm.substring(4));
        //获取 日历 对象
        Calendar calendar = Calendar.getInstance();
        //填充年
        calendar.set(Calendar.YEAR, year);
        //填充月 calenda里的月份是0-11
        calendar.set(Calendar.MONTH, month - 1);
        //获取2020-02日历的最大字段，也就是最大多少天
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        //填充 最后一天
        calendar.set(Calendar.DAY_OF_MONTH, maxDay);
        SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMdd"); //格式化当前系统日期
        return dateFm.format(calendar.getTime());
    }


    /**
     * 获取周末日期
     *
     * @param yyyymmdd 周开始日期
     * @return 周结束日期
     */
    public static String getWeekLastDay(String yyyymmdd) {
        int year = Integer.parseInt(yyyymmdd.substring(0, 4));
        int month = Integer.parseInt(yyyymmdd.substring(4, 6));
        int day = Integer.parseInt(yyyymmdd.substring(6));
        //获取 日历 对象
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, day);
        calendar.add(Calendar.DATE, 6);
        SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMdd"); //格式化当前系统日期
        return dateFm.format(calendar.getTime());
    }

    /**
     * 获取当前日期
     *
     * @return 当前日期
     */
    public static String getCurrentDay() {
        SimpleDateFormat dateFm = new SimpleDateFormat("yyyyMMddHHmmss"); //格式化当前系统日期
        return dateFm.format(new Date());
    }

    public static Date getNextDay(int nDay) {
        //获取 日历 对象
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, nDay);
        return calendar.getTime();
    }


    /**
     * 日期字符串转日期类型
     *
     * @param date 日期字符
     * @return 日期类型
     */
    public static Date stringToDateTime(String date) {
        Date dRtn = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//注意月份是MM
        try {
            dRtn = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dRtn;
    }

    /**
     * 日期字符串转日期类型
     *
     * @param date 日期字符
     * @return 日期类型
     */
    public static Date stringUtfToDateTime(String date) {
        Date dRtn = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");//注意月份是MM
        try {
            dRtn = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dRtn;
    }

    /**
     * 生成UUID编码
     *
     * @return 大写UUID
     */
    public static String createUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }


    /**
     * 获取实际客户端IP地址
     *
     * @param request 访问请求
     * @return 客户端IP地址
     */
    public static String getRemoteIP(HttpServletRequest request) {
        if (null == request.getHeader("x-forwarded-for")) {
            return request.getRemoteAddr();
        }
        return request.getHeader("x-forwarded-for");
    }


    /**
     * 获得客户端真实IP地址
     *
     * @param request 访问请求
     * @return 客户端真实IP地址
     */
    public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
