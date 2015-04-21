package com.xiaoqq.practise.threadmonitor.uuid;

import java.util.UUID;

/**
 * Created by rxiao on 4/21/15.
 */
public abstract class HashUtil {

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getObjectHash(Object obj) {
        return ""+System.identityHashCode(obj);
    }

    public static String getCurrentThreadHash() {
        return getObjectHash(Thread.currentThread());
    }
}
