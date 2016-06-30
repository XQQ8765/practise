package com.xiaoqq.stockwin;

import com.xiaoqq.stockwin.model.AveNWeekRule;
import com.xiaoqq.stockwin.model.Rule;
import com.xiaoqq.stockwin.model.RuleEngine;
import com.xiaoqq.stockwin.model.StockRoot;
import com.xiaoqq.stockwin.util.HttpVisitorUtil;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Integer stockCode = 2100;//000100
        int stockNum = 100;
        RuleEngine ruleEngine = new RuleEngine();
        Rule avgNWeekRule = new AveNWeekRule(60);
        ruleEngine.addRule(avgNWeekRule);

        for (int i=0; i<stockNum; ++i) {
            //String code = "600303";
            //String code = "000089";
            //String code = "000100";
            //String code = ""+stockCode;
            String code = numberToStockCode(stockCode);
            StockRoot stockRoot = new StockVisit(code).visit();
            ruleEngine.run(stockRoot);
            stockCode++;
        }
    }

    private static String numberToStockCode(Integer myInt) {
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
}
