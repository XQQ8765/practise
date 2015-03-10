package com.xqq.asm.introduceInterface;

/**
 * Created by xiaoqq on 15-3-9.
 */
public abstract class C implements Comparable<C>{
    private int v;

    public C(int v) {
        this.v = v;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public int compareTo(C o) {
        return this.v - o.v;
    }

    //Inner class
    private static final class Inner {

    }
}
