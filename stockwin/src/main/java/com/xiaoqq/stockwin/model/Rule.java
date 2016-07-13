package com.xiaoqq.stockwin.model;

/**
 * Created by rxiao on 6/30/2016.
 */
public abstract class Rule {
    public static final int HIGH = 100;
    public static final int MID = 50;
    public abstract boolean match(StockRoot stock);
    public abstract void perform(StockRoot stock);
    public abstract void afterPerformAll();
    public abstract String getName();
    public abstract String getDescription();
    public abstract Integer priority();
}
