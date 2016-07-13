package com.xiaoqq.stockwin.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rxiao on 7/13/2016.
 */
public class StockCodeUtil {

    public static List<Integer> getStockCodeList() {
        List<Integer> stockCodeList = new ArrayList<Integer>();
        addCodeForRange(stockCodeList, "000001", 999);
        addCodeForRange(stockCodeList, "002000", 999);
        addCodeForRange(stockCodeList, "200000", 999);
        addCodeForRange(stockCodeList, "600000", 999);
        return stockCodeList;
    }


    public static String numberToStockCode(Integer myInt) {
        String str = "" + myInt;
        if (str.length() < 6) {
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<6-str.length(); ++i) {
                sb.append('0');
            }
            sb.append(str);
            return sb.toString();
        }
        return str.substring(0, 6);
    }

    private static void addCodeForRange(List<Integer> stockCodeList, String codeStr, int count) {
        Integer startCode = Integer.parseInt(codeStr);
        for (int i=0; i<count; ++i) {
            Integer code = startCode + i;
            if (!isInvalidStockCode(code)) {
                stockCodeList.add(code);
            }
        }
    }
    private static boolean isInvalidStockCode(Integer code) {
        List<Range> invalidCodeRangeList = new ArrayList<>();

        //000XXX should be excluded
        addUniqueRangeItem(invalidCodeRangeList, "000041");
        addUniqueRangeItem(invalidCodeRangeList, "000044");
        addRangeItem(invalidCodeRangeList, "000051", "000054");
        addUniqueRangeItem(invalidCodeRangeList, "000057");
        addUniqueRangeItem(invalidCodeRangeList, "000064");
        addUniqueRangeItem(invalidCodeRangeList, "000067");
        addRangeItem(invalidCodeRangeList, "000071", "000077");
        addRangeItem(invalidCodeRangeList, "000079", "000087");
        addRangeItem(invalidCodeRangeList, "000091", "000095");
        addRangeItem(invalidCodeRangeList, "000097", "000098");
        addRangeItem(invalidCodeRangeList, "000101", "000149");
        addRangeItem(invalidCodeRangeList, "000152", "000152");
        addRangeItem(invalidCodeRangeList, "000154", "000154");
        addRangeItem(invalidCodeRangeList, "000160", "000164");
        addRangeItem(invalidCodeRangeList, "000165", "000165");
        addRangeItemWithExcludedItems(invalidCodeRangeList, "000167", "000399", "000301", "000333", "000338");//000301, 000333, 000338
        addUniqueRangeItem(invalidCodeRangeList, "000414");
        addUniqueRangeItem(invalidCodeRangeList, "000424");
        addUniqueRangeItem(invalidCodeRangeList, "000427");
        addRangeItemWithExcludedItems(invalidCodeRangeList, "000431", "000500", "000488", "000498");//000488, 000498
        addUniqueRangeItem(invalidCodeRangeList, "000512");
        addRangeItemWithExcludedItems(invalidCodeRangeList, "000574", "000580", "000576", "000578");//000576, 000578
        addUniqueRangeItem(invalidCodeRangeList, "000604");
        addUniqueRangeItem(invalidCodeRangeList, "000614");
        addUniqueRangeItem(invalidCodeRangeList, "000624");
        addUniqueRangeItem(invalidCodeRangeList, "000634");
        addRangeItem(invalidCodeRangeList, "000640", "000649");
        addUniqueRangeItem(invalidCodeRangeList, "000654");
        addUniqueRangeItem(invalidCodeRangeList, "000664");
        addUniqueRangeItem(invalidCodeRangeList, "000674");
        addUniqueRangeItem(invalidCodeRangeList, "000684");
        addUniqueRangeItem(invalidCodeRangeList, "000694");
        addUniqueRangeItem(invalidCodeRangeList, "000696");
        addUniqueRangeItem(invalidCodeRangeList, "000704");
        addUniqueRangeItem(invalidCodeRangeList, "000706");
        addUniqueRangeItem(invalidCodeRangeList, "000714");
        addUniqueRangeItem(invalidCodeRangeList, "000724");
        addUniqueRangeItem(invalidCodeRangeList, "000734");
        addRangeItem(invalidCodeRangeList, "000740", "000749");
        addUniqueRangeItem(invalidCodeRangeList, "000754");
        addUniqueRangeItem(invalidCodeRangeList, "000764");
        addRangeItem(invalidCodeRangeList, "000770", "000775");
        addUniqueRangeItem(invalidCodeRangeList, "000781");
        addUniqueRangeItem(invalidCodeRangeList, "000784");
        addUniqueRangeItem(invalidCodeRangeList, "000794");
        addUniqueRangeItem(invalidCodeRangeList, "000804");
        addUniqueRangeItem(invalidCodeRangeList, "000808");
        addUniqueRangeItem(invalidCodeRangeList, "000814");
        addUniqueRangeItem(invalidCodeRangeList, "000824");
        addUniqueRangeItem(invalidCodeRangeList, "000834");
        addRangeItem(invalidCodeRangeList, "000840", "000847");
        addUniqueRangeItem(invalidCodeRangeList, "000849");
        addRangeItem(invalidCodeRangeList, "000853", "000855");
        addUniqueRangeItem(invalidCodeRangeList, "000857");
        addRangeItem(invalidCodeRangeList, "000864", "000865");
        addUniqueRangeItem(invalidCodeRangeList, "000867");
        addRangeItem(invalidCodeRangeList, "000870", "000874");
        addUniqueRangeItem(invalidCodeRangeList, "000879");
        addUniqueRangeItem(invalidCodeRangeList, "000884");
        addUniqueRangeItem(invalidCodeRangeList, "000891");
        addUniqueRangeItem(invalidCodeRangeList, "000894");
        addUniqueRangeItem(invalidCodeRangeList, "000896");
        addUniqueRangeItem(invalidCodeRangeList, "000904");
        addUniqueRangeItem(invalidCodeRangeList, "000907");
        addUniqueRangeItem(invalidCodeRangeList, "000914");
        addUniqueRangeItem(invalidCodeRangeList, "000924");
        addUniqueRangeItem(invalidCodeRangeList, "000934");
        addRangeItem(invalidCodeRangeList, "000940", "000947");
        addUniqueRangeItem(invalidCodeRangeList, "000954");
        addUniqueRangeItem(invalidCodeRangeList, "000964");
        addUniqueRangeItem(invalidCodeRangeList, "000974");
        addUniqueRangeItem(invalidCodeRangeList, "000984");
        addUniqueRangeItem(invalidCodeRangeList, "000986");
        addUniqueRangeItem(invalidCodeRangeList, "000991");
        addUniqueRangeItem(invalidCodeRangeList, "000992");
        addUniqueRangeItem(invalidCodeRangeList, "000994");

        //200XXX should be excluded
        addUniqueRangeItem(invalidCodeRangeList, "002000");

        for (Range range : invalidCodeRangeList) {
            if (range.isInclude(code)) {
                return true;
            }
        }
        return false;
    }

    private static void addRangeItem(List<Range> invalidCodeRangeList, String startcode, String endcode) {
        Range range = new Range(Integer.parseInt(startcode), Integer.parseInt(endcode));
        if (invalidCodeRangeList != null) {
            invalidCodeRangeList.add(range);
        }
    }

    private static void addRangeItemWithExcludedItems(List<Range> invalidCodeRangeList, String startcode, String endcode, String... excludedCodes) {
        List<Integer> excludedItems = new ArrayList<>();
        if (excludedCodes != null) {
            for (String excludedCode: excludedCodes) {
                Integer code = Integer.parseInt(excludedCode);
                excludedItems.add(code);
            }
        }
        Range range = new Range(Integer.parseInt(startcode), Integer.parseInt(endcode), excludedItems);
        if (invalidCodeRangeList != null) {
            invalidCodeRangeList.add(range);
        }
    }

    private static void addUniqueRangeItem(List<Range> invalidCodeRangeList, String startcode) {
        Integer code = Integer.parseInt(startcode);
        Range range = new Range(code, code);
        if (invalidCodeRangeList != null) {
            invalidCodeRangeList.add(range);
        }
    }

    private static class Range {
        Integer start;
        Integer end;
        //Items in List "excludedItems" will be treated as not included in the range.
        List<Integer> excludedItems;

        public Range(Integer start, Integer end) {
            this.start = start;
            this.end = end;
        }

        public Range(Integer start, Integer end, List<Integer> excludedItems) {
            this.start = start;
            this.end = end;
            this.excludedItems = excludedItems;
        }

        boolean isInclude(Integer num) {
            if (excludedItems != null) {
                if (excludedItems.contains(num)) {
                    return false;
                }
            }
            return (num >= start && num <= end);
        }
    }

}
