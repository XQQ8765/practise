package com.xiaoqq.stockwin;

import com.xiaoqq.stockwin.model.StockRoot;
import com.xiaoqq.stockwin.util.HttpVisitorUtil;
import com.xiaoqq.stockwin.util.StockJsonParser;

/**
 * Created by rxiao on 6/30/2016.
 */
public class StockVisit {
    private String code;

    public StockVisit(String code) {
        this.code = code;
    }

    public StockRoot visit() {
        try {
            return doVisit();
        } catch (Exception e) {
            System.err.print("!!!Error happens. Failed to get stock info for code:" + code + ", Exception:" + e.getMessage());
            return null;
        }
    }

    private StockRoot doVisit() throws Exception {
        String url = buildURL(code);
        //System.out.println("Visit for code:" + code);
        //System.out.println("Visit url:" + url);
        String json = HttpVisitorUtil.doGet(url);
        //System.out.println("json:" + json);
        StockRoot stockRoot = StockJsonParser.getInstance(json).getStockRoot();
        if (stockRoot == null) {
            System.err.println("!!!Failed to get stock info for code:" + code);
        }
        return stockRoot;
    }

    private String buildURL(String code) {
        return "http://gp.3g.qq.com/g/stock/wap3/ajax_server/json.jsp?action=getQuote&sid=&need_kchart=true&securities_id=share_" + code + ".xshe";
    }
}
