package com.xqq.asm.practise2;

/**
 * Used by "AddToSubClassAdpter".
 */
public class Calculate {

    public static int add(int i, int j) {
        return i+j;
    }

    public static float add(float i, float j) {
        return i+j;
    }

    public static double add(double i, double j) {
        return i+j;
    }

    public int addInst(int i, int j) {
        return i+j;
    }
}
