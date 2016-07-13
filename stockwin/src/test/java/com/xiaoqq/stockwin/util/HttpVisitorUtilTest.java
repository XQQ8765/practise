package com.xiaoqq.stockwin.util;

import com.xiaoqq.stockwin.util.HttpVisitorUtil;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;

/**
 * Created by rxiao on 6/30/2016.
 */
public class HttpVisitorUtilTest {
    @Test
    public void testDoGet() throws Exception {
        //String stock = "000041";
        String stock = "000089";
        String url = "http://gp.3g.qq.com/g/stock/wap3/ajax_server/json.jsp?action=getQuote&sid=&need_kchart=true&securities_id=share_" + stock + ".xshe";
        String result = HttpVisitorUtil.doGet(url);
        System.out.println(result);
    }
}
