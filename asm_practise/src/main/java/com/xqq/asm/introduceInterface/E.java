package com.xqq.asm.introduceInterface;

import com.xqq.asm.util.ClassUtil;

import java.io.IOException;

/**
 * Example class
 */
public class E extends C implements IAdd{

    public E(int v) {
        super(v);
    }

    public int add(int i) {
        int tmpV = getV() + i;
        setV(tmpV);
        return tmpV;
    }

    public static void main(String[] args) throws IOException {
        ClassUtil.printWithTraceClassVisit(E.class);
    }
}
