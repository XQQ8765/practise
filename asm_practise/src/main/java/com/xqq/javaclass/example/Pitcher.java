package com.xqq.javaclass.example;

/**
 * Created by rxiao on 4/14/15.
 */
public class Pitcher {

    private static Ball ball = new Ball();

    static void playBall() {
        int i = 0;
        for (;;) {
            try {
                if (i%4 == 3) {
                    throw ball;
                }
                ++i;
            } catch (Ball b) {
                i = 0;
            }

        }
    }
}

class Ball extends Exception {

}
