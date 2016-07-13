package com.xiaoqq.stockwin.util;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.InvalidPathException;
import com.xiaoqq.stockwin.model.KChartItem;
import com.xiaoqq.stockwin.model.StockRoot;
import net.minidev.json.JSONArray;

import java.util.*;

/**
 * Created by rxiao on 6/30/2016.
 */
public class StockJsonParser {
    private String json;
    private JsonPathUtil jsonPathUtil;
    private static final String listSeparator = "\n";

    private StockJsonParser(String json)
    {
        this.json = json;
        this.jsonPathUtil = new JsonPathUtil();
    }

    public static StockJsonParser getInstance(String json)
    {
        return new StockJsonParser(json);
    }

    public StockRoot getStockRoot() throws Exception
    {
        try {
            if (json == null || json.isEmpty()) {
                return null;
            }
           return parse();
        } catch (Exception e) {
            System.err.println("!!! Failed to parse json data." + e.getMessage());
            return null;
        }
    }

    private StockRoot parse() throws Exception {
        StockRoot stock = new StockRoot();
        stock.setUpdateDate(new Date());//TODO: fetch from Json
        String code = getNodeValue("$.code");
        if (code == null) {
            return null;
        }
        stock.setCode(code);
        stock.setCurPrice(getDoubleNodeValue("$.price"));
        stock.setName(getNodeValue("$.name"));
        //stock.setDailyList(parseKChartItemList("$.kcahrt.daily"));
        stock.setWeeklyList(parseKChartItemList("$.kcahrt.weekly"));
        stock.setMonthlyList(parseKChartItemList("$.kcahrt.monthly"));
        return stock;
    }

    private List<KChartItem> parseKChartItemList(String path) {
        List<KChartItem> items = new ArrayList<>();
        JSONArray jsonArray = jsonPathUtil.extractArray(path, json, listSeparator);
        if (jsonArray != null) {
            for (int i = 0; i<jsonArray.size(); ++i) {
                JSONArray item = (JSONArray) jsonArray.get(i);
                String dateStr = (String) item.get(0);//2015-03-06
                Date date = DateUtil.convertToDateWithYMD(dateStr);
                double price = convertToDouble((String) item.get(2));//ÊÕÅÌ¼Û

                KChartItem kChartItem = new KChartItem();
                kChartItem.setDate(date);
                kChartItem.setPrice(price);
                items.add(kChartItem);
            }
        }
        return items;
    }

    private List<String> getChildListNodeValue(String expressionString, String documentString, String listSeparator)
    {
        String value = getChildNodeValue(expressionString, documentString, listSeparator);
        return value == null ? Collections.EMPTY_LIST : Arrays.asList(value.split(listSeparator));
    }

    private String getChildNodeValue(String expressionString, String documentString, String listSeparator)
    {
        String value = null;
        try {
            value = jsonPathUtil.extract(expressionString, documentString, listSeparator);
        } catch (InvalidPathException e) {
            //System.err.print("The json path [" + expressionString + "] is invalid for the json string " + documentString + e.getMessage());
        } catch (InvalidJsonException x) {
            //System.err.print("Invalid Json String." + x.getMessage());
        }
        return value;
    }

    private double getDoubleNodeValue(String expressionString)
    {
        return Double.valueOf(getNodeValue(expressionString));
    }

    private static Double convertToDouble(String str) {
        return Double.valueOf(str);
    }

    private String getNodeValue(String expressionString)
    {
        String value = null;
        try {
            value = jsonPathUtil.extract(expressionString, json, null);
        } catch (InvalidPathException e) {
            //System.err.println("The json path [" + expressionString + "] is invalid." + e.getMessage());
        } catch (InvalidJsonException x) {
            //System.err.println("Invalid Json String." + x.getMessage());
        }
        return value;
    }
}
