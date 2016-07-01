package com.xiaoqq.stockwin.util;

public class NumberUtil {
    public static String formatDouble(Double d) {
        if (d == null) {
            return "";
        }
        return new java.text.DecimalFormat("#.00").format(d);
    }
}
