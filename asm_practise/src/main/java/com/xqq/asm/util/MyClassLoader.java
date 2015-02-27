package com.xqq.asm.util;

/**
 * Created by rxiao on 2/26/15.
 */

public class MyClassLoader extends ClassLoader {
    public Class defineClass(String name, byte[] b) {
        return defineClass(name, b, 0, b.length);
    }
}
