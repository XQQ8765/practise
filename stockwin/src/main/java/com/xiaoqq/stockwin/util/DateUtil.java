package com.xiaoqq.stockwin.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rxiao on 6/30/2016.
 */
public class DateUtil {
    public static Date convertToDateWithYMD(String dateStr) {
        return convertToDate(dateStr, "yyyy-MM-dd");
    }
    public static Date convertToDate(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            System.err.println("!!!Failed to convert dateStr:" + dateStr + " to Date with format:" + format);
            return null;
        }
    }
}
