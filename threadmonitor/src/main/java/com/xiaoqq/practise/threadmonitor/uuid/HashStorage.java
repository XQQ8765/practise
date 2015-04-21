package com.xiaoqq.practise.threadmonitor.uuid;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rxiao on 4/21/15.
 */
public abstract class HashStorage {
    private static ConcurrentHashMap<String, String> hashMap = new ConcurrentHashMap<String, String>();
    private HashStorage() {

    }

    public static void put(String objectHashId, String uuid) {
        hashMap.put(objectHashId, uuid);
    }

    public static String getByObjectHashId(String objectHashId) {
        return hashMap.get(objectHashId);
    }

    public static boolean contains(String objectHashId) {
        return hashMap.containsKey(objectHashId);
    }

    public static String remove(String objectHashId) {
        return hashMap.remove(objectHashId);
    }
}
