package com.xqq.asm.practise2;

/**
 * Used by Class: BeanWriterWithCoreAPI, ClassTransformTest
 */
public class Bean {
    private int f;
    public int getF() {
        return f;
    }
    public void setF(int f) {
        this.f = f;
    }
    public void checkAndSetF(int f) {
        if (f >= 1 && f <= 10) {
            this.f = f;
        } else {
            String errorMsg = "Only 1<=f<=10 is allowed.";
            System.err.println(errorMsg);
            //throw new IllegalArgumentException(errorMsg);
        }
    }
}