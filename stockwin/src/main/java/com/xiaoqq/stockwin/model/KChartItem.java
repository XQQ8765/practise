package com.xiaoqq.stockwin.model;

import java.util.Date;

/**
 * Created by rxiao on 6/30/2016.
 */
public class KChartItem {
    Date date;
    double price;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
