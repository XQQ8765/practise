package com.xqq.asm.introduceInterface;

import com.xqq.asm.util.ClassUtil;

import java.io.IOException;

/**
 * Created by xiaoqq on 15-3-9.
 */
public class E extends C implements IAdd{

    public E(int v) {
        super(v);
    }

    @Override
    public int add(int i) {
        int tmpV = getV() + i;
        setV(tmpV);
        return tmpV;
    }

    public static void main(String[] args) throws IOException {
        ClassUtil.printWithTraceClassVisit("com.xqq.asm.introduceInterface.E");
    }
}
