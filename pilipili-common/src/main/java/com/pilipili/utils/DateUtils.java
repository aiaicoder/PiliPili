package com.pilipili.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2024/6/22 20:36
 */
public class DateUtils {
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYYMMDD="yyyyMMdd";
    public static final String _YYYYMMDD="yyyy/MM/dd";
    public static final String YYYYMM="yyyyMM";

    public static String format(Date date, String patten) {
        return new SimpleDateFormat(patten).format(date);
    }

    public static Date parse(String date, String patten) {
        try {
            return new SimpleDateFormat(patten).parse(date);
        } catch (Exception e) {
            return null;
        }
    }
}
