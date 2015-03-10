package com.xqq.asm.rename;

/**
 * We would like to do following operations:
 * <ul>
 *     <li>Rename field "v" to "k"</li>
 *     <li>Rename method "getV" to "getK"</li>
 *     <li>Rename field "setV" to "setK"</li>
 * </ul>
 * method getK() to
 */
public class C {
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

    public boolean greaterThan(int v) {
        return getV() > v;
    }

    public boolean between(int i, int j) {
        return (getV() > i && getV() < j);
    }

    public void add(int v) {
        int t = getV() + v;
        setV(t);
    }

    public static class InnerC {
        public static boolean lessThan100(int i) {
            C c = new C(i);
            return  c.getV() < 100;
        }
    }
}
