package com.xqq.asm.rename;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rxiao on 3/12/15.
 */
public class RenameMapUtil {
    private static Map<String, String> renameMethodMap;
    private static Map<String, String> renameFieldMap;

    static {
        renameFieldMap = new HashMap<String, String>(){};
        renameFieldMap.put("v", "k");

        renameMethodMap = new HashMap<String, String>(){};
        renameMethodMap.put("getV", "getK");
        renameMethodMap.put("setV", "setK");
    }

    public static String getReplacedFieldName(String name) {
        if (renameFieldMap.containsKey(name)) {
            return renameFieldMap.get(name);
        }
        return name;
    }

    public static String getReplacedMethodName(String name) {
        if (renameMethodMap.containsKey(name)) {
            return renameMethodMap.get(name);
        }
        return name;
    }
}
