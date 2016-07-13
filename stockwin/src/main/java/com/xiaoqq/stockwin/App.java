package com.xiaoqq.stockwin;

import com.xiaoqq.stockwin.model.AveNWeekMonthRule;
import com.xiaoqq.stockwin.model.Rule;
import com.xiaoqq.stockwin.model.RuleEngine;
import com.xiaoqq.stockwin.model.StockRoot;
import com.xiaoqq.stockwin.util.StockCodeUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        RuleEngine ruleEngine = new RuleEngine();
        Rule avgNWeekRule = new AveNWeekMonthRule(60, 60);
        ruleEngine.addRule(avgNWeekRule);

        List<Integer> stockCodeList = StockCodeUtil.getStockCodeList();
        for (Integer codeInt: stockCodeList) {
            String code = StockCodeUtil.numberToStockCode(codeInt);
            StockRoot stockRoot = new StockVisit(code).visit();
            ruleEngine.run(stockRoot);
        }
        ruleEngine.afterRunAll();
    }


}
