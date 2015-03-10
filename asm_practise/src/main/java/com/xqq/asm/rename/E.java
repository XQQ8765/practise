package com.xqq.asm.rename;

import com.xqq.asm.util.ClassUtil;

import java.io.IOException;

/**
 * Example class
 */
public class E {
    private int k;

    public E(int k) {
        this.k = k;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public boolean greaterThan(int v) {
        return getK() > v;
    }

    public boolean between(int i, int j) {
        return (getK() > i && getK() < j);
    }

    public void add(int v) {
        int t = getK() + v;
        setK(t);
    }


    public static class InnerC {
        public static boolean lessThan100(int i) {
            E e = new E(i);
            return  e.getK() < 100;
        }
    }

    public static void main(String[] args) throws IOException {
        //Class clazz = E.class;
        Class clazz = E.InnerC.class;
        ClassUtil.printWithTraceClassVisit(clazz);
        System.out.println();
        System.out.println("------------------------------------------------------------------------------");
        System.out.println();
        ClassUtil.asmifierWithTraceClassVisit(clazz);
    }
}
