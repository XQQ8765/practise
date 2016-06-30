package com.xiaoqq.stockwin.util;

import com.xiaoqq.stockwin.model.StockRoot;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by rxiao on 6/30/2016.
 */
public class StockJsonParserTest {

    @Test
    public void testGetStockRoot() throws Exception {

        String fileName = "D:\\workspace\\practise\\stockwin\\src\\test\\java\\com\\xiaoqq\\stockwin\\util\\stock_000089.json";
        File file = new File(fileName);
        String json = FileUtils.readFileToString(file, "UTF-8");
        //String url = "http://gp.3g.qq.com/g/stock/wap3/ajax_server/json.jsp?action=getQuote&sid=&need_kchart=true&securities_id=share_000089.xshe";
        //String json = HttpVisitorUtil.doGet(url);
        //System.out.println(json);

        StockRoot stockRoot = StockJsonParser.getInstance(json).getStockRoot();
        Assert.assertEquals("code is the same.", "000089.SZ", stockRoot.getCode());
        Assert.assertEquals("name is the same.", "ÉîÛÚ»ú³¡", stockRoot.getName());
    }
}
