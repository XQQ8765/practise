package com.xqq.asm.mergetwoclasses;

/**
 * Created by rxiao on 3/10/15.
 */
public class C1 implements Runnable{
    private int f;

    public C1(int f) {
        this.f = f;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public void run() {
        System.out.println(f);
    }
}
