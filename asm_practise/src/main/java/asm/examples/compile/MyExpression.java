package asm.examples.compile;

/**
 * Created by rxiao on 3/6/15.
 */
public class MyExpression implements Expression{
    public int eval(int i, int j) {
        return (i > 3 && 6 > i) ? 1 : 0;
    }
}
