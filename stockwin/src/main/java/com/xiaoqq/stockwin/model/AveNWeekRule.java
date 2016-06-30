package com.xiaoqq.stockwin.model;

import java.util.List;

/**
 * Created by rxiao on 6/30/2016.
 */
public class AveNWeekRule extends Rule {
    private Integer num;//平均线天数
    private Double avgPrice;

    public AveNWeekRule(Integer num) {
        this.num = num;
    }

    @Override
    public boolean match(StockRoot stock) {
        this.avgPrice = calNWeekAvgPrice(stock);
        Double curPrice = stock.getCurPrice();
        if (curPrice == null) {
            return false;
        }
        if (curPrice > avgPrice) {
            return false;
        }

        Double preWeekPrice = getPreWeekPrice(stock);
        if (preWeekPrice == null) {
            return false;
        }
        return 100 * ((curPrice - preWeekPrice)/preWeekPrice) > 5;//比上周股价上涨超过5%
    }

    @Override
    public void perform(StockRoot stock) {
        Double curPrice = stock.getCurPrice();
        Double diff = 100 * (avgPrice- curPrice)/curPrice;
        System.out.println(stock.getCode() + "  |  " + stock.getName() + " | " + diff);
    }

    @Override
    public String getName() {
        return "Lower than " + num + " Week and increase fast";
    }

    @Override
    public String getDescription() {
        return "股价低于" + num + "日平均线，且最近一周涨幅>5%.";
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

    private Double calNWeekAvgPrice(StockRoot stock) {
        List<KChartItem> itemList = stock.getWeeklyList();
        if (itemList == null || itemList.size() < num) {
            return null;
        }
        Double sum = 0d;
        for (int i = 1; i < num+1; i++) {
            sum += itemList.get(itemList.size() - i).getPrice();
        }
        return sum/num;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
