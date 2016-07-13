package com.xiaoqq.stockwin.model;

import com.xiaoqq.stockwin.util.DateUtil;
import com.xiaoqq.stockwin.util.NumberUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rxiao on 6/30/2016.
 */
public class AveNWeekMonthRule extends Rule {
    private Integer nWeek;//Avg weeks
    private Integer nMonth;//Abg monthly
    private Double avgNWeekPrice;
    private Double avgNMonthPrice;
    private boolean printHeader;
    private static final Double INCREASE_PERCENT = 5d;

    private Integer matchedCount = 0;
    private Integer processedCount = 0;

    public AveNWeekMonthRule(Integer nWeek, Integer nMonth) {
        this.nWeek = nWeek;
        this.nMonth = nMonth;
        printHeader = true;
    }

    @Override
    public boolean match(StockRoot stock) {
        this.processedCount += 1;
        Double curPrice = stock.getCurPrice();
        if (curPrice == null) {
            return false;
        }

        this.avgNMonthPrice = calNMonthAvgPrice(stock, nMonth);
        if (avgNMonthPrice == null) {
            return false;
        }
        if (curPrice > avgNMonthPrice) {
            return false;
        }

        this.avgNWeekPrice = calNWeekAvgPrice(stock, nWeek);
        if (avgNWeekPrice == null) {
            return false;
        }
        if (curPrice > avgNWeekPrice) {
            return false;
        }

        Double preWeekPrice = getPreWeekPrice(stock);
        if (preWeekPrice == null) {
            return false;
        }
        boolean matched = 100 * ((curPrice - preWeekPrice)/preWeekPrice) > INCREASE_PERCENT;//比上周股价上涨超过5%
        return matched;
    }

    @Override
    public void perform(StockRoot stock) {
        if (printHeader) {
            output("Today is: " + DateUtil.formatDate(new Date()));
            output("The rule is: " + getDescription());
            output(new Object[]{"Code", "Name", "<" + getnWeek() +" Week Diff", "<" + getnMonth() +" Month Diff"});
            printHeader = false;
        }
        Double curPrice = stock.getCurPrice();
        Double weekDiff = 100 * (avgNWeekPrice - curPrice)/curPrice;
        String weekDiffStr = NumberUtil.formatDouble(weekDiff);
        Double monthDiff = 100 * (avgNMonthPrice - curPrice)/curPrice;
        String monthDiffStr = NumberUtil.formatDouble(monthDiff);
        output(new Object[]{stock.getCode(), stock.getName(), weekDiffStr, monthDiffStr});

        this.matchedCount += 1;
    }

    @Override
    public void afterPerformAll() {
        output("Rule : " + getDescription() + " processed " + this.processedCount + " stocks, matched " + this.matchedCount + " stocks.");
    }

    private void output(Object[] objs) {
        if (objs != null) {
            StringBuilder sb = new StringBuilder();
            for (Object obj : objs) {
                sb.append(obj);
                sb.append(" | ");
            }
            output(sb.toString());
        }
    }

    private void output(String str) {
        System.out.println(str);
    }
    @Override
    public String getName() {
        return "Lower than " + nWeek + " Week and increase fast";
    }

    @Override
    public String getDescription() {
        return "Price <(" + nWeek + ")Week, <(" + nMonth +")Month, Increase percent>"+INCREASE_PERCENT+"% in this week.";
    }

    @Override
    public Integer priority() {
        return HIGH;
    }

    private Double getPreWeekPrice(StockRoot stock) {
        List<KChartItem> itemList = stock.getWeeklyList();
        if (itemList == null || itemList.size() < 2) {
            return null;
        }
        return  itemList.get(itemList.size() - 2).getPrice();
    }

    private Double calNWeekAvgPrice(StockRoot stock, Integer n) {
        return calNAvgPrice(stock.getWeeklyList(), n);
    }

    private Double calNMonthAvgPrice(StockRoot stock, Integer n) {
        return calNAvgPrice(stock.getMonthlyList(), n);
    }

    private Double calNAvgPrice(List<KChartItem> itemList, Integer n) {
        if (itemList == null || itemList.size() < n || n <= 0) {
            return null;
        }
        Double sum = 0d;
        for (int i = 1; i < n+1; i++) {
            sum += itemList.get(itemList.size() - i).getPrice();
        }
        return sum/n;
    }

    public Integer getnWeek() {
        return nWeek;
    }

    public void setnWeek(Integer nWeek) {
        this.nWeek = nWeek;
    }

    public Integer getnMonth() {
        return nMonth;
    }

    public void setnMonth(Integer nMonth) {
        this.nMonth = nMonth;
    }
}
