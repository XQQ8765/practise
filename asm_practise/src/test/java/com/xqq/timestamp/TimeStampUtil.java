package com.xqq.timestamp;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * Created by rxiao on 3/13/15.
 */
public class TimeStampUtil {

    public static String formatTimestamp(long timestamp)
    {
        return DateFormatUtils.ISO_DATETIME_FORMAT.format(timestamp);
    }

}
