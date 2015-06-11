package com.xiaoqq.practise.threadmonitor.relationship;

/**
 * Created by rxiao on 6/11/15.
 */
public class MethodUtil {

    public Object getCurrentObject(boolean isStaticMethod, String className, ClassLoader classLoader) {
        if (isStaticMethod) {
            try {
                return Class.forName(className, false, classLoader);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return this;
        }
    }
}
