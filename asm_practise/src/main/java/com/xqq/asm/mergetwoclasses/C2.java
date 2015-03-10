package com.xqq.asm.mergetwoclasses;

import java.util.concurrent.Callable;

/**
 * Created by rxiao on 3/10/15.
 */
public class C2 implements Callable<C2> {
    private int f;
    private String name;

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public C2(int f, String name) {

        this.f = f;
        this.name = name;
    }

    public String nameToUpperCase() {
        if (name != null) {
            return name.toUpperCase();
        }
        return name;
    }

    public C2 call() throws Exception {
        System.out.println("C2: f:" + f + ", name:" + name);
        return this;
    }
}
