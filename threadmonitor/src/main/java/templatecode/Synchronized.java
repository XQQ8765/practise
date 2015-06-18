package templatecode;

/**
 * Created by rxiao on 6/18/15.
 */
public class Synchronized {

    public void testStaticSynchronized() {
        synchronized (Synchronized.class) {
            int i = 0;
        }
    }

    public void testSynchronized() {
        Synchronized obj = new Synchronized();
        synchronized(obj) {
            int i = 0;
        }
    }

    public static synchronized int staticAdd1(int i) {
        return i+1;
    }

    public synchronized int add2(int i) {
        return i+2;
    }

    public void callStaticAdd1() {
        staticAdd1(10);
    }

    public static void staticCallStaticAdd1() {
        staticAdd1(10);
    }

    public void callAdd2() {
        add2(10);
    }

    public static void staticCallAdd2() {
        Synchronized obj = new Synchronized();
        synchronized (obj) {
            obj.add2(10);
        }
    }

}
