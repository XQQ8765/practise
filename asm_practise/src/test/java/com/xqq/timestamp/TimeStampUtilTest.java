package com.xqq.timestamp;

import org.junit.Test;

/**
 * Created by rxiao on 3/13/15.
 */
public class TimeStampUtilTest {
    @Test
    public void testFormater() throws Exception {
        Long [] timestamps = new Long[] {
          /* 1424970300000L, 1424995200000L,
                1424995200000L, 1425081600000L,
                1425081600000L, 1425168000000L,
                1425168000000L, 1425254400000L,
                1425254400000L, 1425340800000L,
                1425340800000L, 1425427200000L,
                1425427200000L, 1425513600000L,
                1425513600000L, 1425594900000L,
                1426042500000L,  1426043400000L
                */
                1426212300000L, 1426212600000L,
                1426090800000L, 1426118400000L
        };

        for (int i=0; i < timestamps.length - 1; i+=2) {
            printConvertedTimeStamp(timestamps[i], timestamps[i+1]);
        }
    }

    private static void printConvertedTimeStamp(Long t1, Long t2) {
        String formatedTime1 = TimeStampUtil.formatTimestamp(t1);
        String formatedTime2 = TimeStampUtil.formatTimestamp(t2);
        System.out.println(t1 + " -> " + t2 + ": " + formatedTime1 + " -> " + formatedTime2);
    }
}
