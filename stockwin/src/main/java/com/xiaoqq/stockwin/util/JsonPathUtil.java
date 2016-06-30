/*
 * Copyright 2008 Quest Software, Inc.
 * ALL RIGHTS RESERVED.
 */

package com.xiaoqq.stockwin.util;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class JsonPathUtil
{
    private final Map<String, JsonPath> expressionCache = new HashMap<String, JsonPath>(16);

    public void clearCache()
    {
        expressionCache.clear();
    }

    public String extract(String expressionString, String documentString, String listSeparator)
    {
        JsonPath expression = getExpression(expressionString);
        if (expression != null) {
            Object object = expression.read(documentString);
            if (object != null) {
                if (object instanceof JSONArray) {
                    JSONArray nodes = (JSONArray)object;
                    if (nodes.size() > 0) {
                        StringBuilder result = new StringBuilder(256);
                        if (nodes.get(0).toString() != null) result.append(nodes.get(0).toString());
                        for (int i = 1; i < nodes.size() && listSeparator != null; i++) {
                            result.append(listSeparator);
                            if (nodes.get(i).toString() != null) result.append(nodes.get(i).toString());
                        }
                        return result.toString();
                    }
                } else {
                    // I've read reports that Strings returned from JsonPath.read are substrings of the entire Json file
                    // This is similar to the issue we found in regex
                    return new String(object.toString());
                }
            }
        }
        return null;
    }

    public JSONArray extractArray(String expressionString, String documentString, String listSeparator)
    {
        JsonPath expression = getExpression(expressionString);
        if (expression != null) {
            Object object = expression.read(documentString);
            if (object != null) {
                if (object instanceof JSONArray) {
                    JSONArray nodes = (JSONArray)object;
                    return nodes;
                    }
                } else {
                    return null;
                }
            }
        return null;
    }

    private JsonPath getExpression(String expressionString)
    {
        JsonPath expression = expressionCache.get(expressionString);
        if (expression == null) {
            expression = JsonPath.compile(expressionString);
            expressionCache.put(expressionString, expression);
        }
        return expression;
    }
}
