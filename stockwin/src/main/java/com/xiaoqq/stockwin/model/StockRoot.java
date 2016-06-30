package com.xiaoqq.stockwin.model;

import java.util.Date;
import java.util.List;

/**
 * Created by rxiao on 6/30/2016.
 */
public class StockRoot {
    private String name;
    private String code;
    private Double curPrice;
    private Date updateDate;

    private List<KChartItem> weeklyList;
    private List<KChartItem> dailyList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getCurPrice() {
        return curPrice;
    }

    public void setCurPrice(Double curPrice) {
        this.curPrice = curPrice;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public List<KChartItem> getWeeklyList() {
        return weeklyList;
    }

    public void setWeeklyList(List<KChartItem> weeklyList) {
        this.weeklyList = weeklyList;
    }

    public List<KChartItem> getDailyList() {
        return dailyList;
    }

    public void setDailyList(List<KChartItem> dailyList) {
        this.dailyList = dailyList;
    }
}
