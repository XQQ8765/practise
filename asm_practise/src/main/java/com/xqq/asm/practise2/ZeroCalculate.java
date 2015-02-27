package com.xqq.asm.practise2;

/**
 * Used by "RemoveAddSubDevideZeroAdapter".
 */
public class ZeroCalculate {

    public static int calculate0() {
        int i = 5 + 0;
        int j = i + 0;
        int k = i - 0;
        int l = i / 0;
        double d = 5d/0;
        return 100;
    }
}
