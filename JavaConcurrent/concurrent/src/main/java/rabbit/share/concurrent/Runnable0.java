package rabbit.share.concurrent;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-6-10
 * Time: 下午10:08
 * To change this template use File | Settings | File Templates.
 */
public class Runnable0 {
    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            public void run() {
                System.out.println(new Random().nextInt(100));
            }
        };

        ExecutorService exec= Executors.newCachedThreadPool();
        for(int i=0;i<5;i++)
            exec.execute(runnable);
        exec.shutdown();
    }
}
