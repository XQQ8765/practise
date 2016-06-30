package com.xiaoqq.stockwin.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rxiao on 6/30/2016.
 */
public class RuleEngine {
    private List<Rule> rules;

    public RuleEngine() {
        rules = new ArrayList<>();
    }

    public void addRule(Rule rule) {
        this.rules.add(rule);
    }

    public void run(StockRoot stock) {
        if (stock == null) {
            return;
        }
        for (Rule rule: rules) {
            if (rule.match(stock)) {
                rule.perform(stock);
            }
        }
    }
}
