package rabbit.share.concurrent;

import java.util.Random;
import java.util.concurrent.*;

/**
 ExecutorService继承自Executor，它的目的是为我们管理Thread对象，从而简化并发编程.
 Executor使我们无需显示的去管理线程的生命周期，是JDK 5之后启动任务的首选方式。
 */

public class CallableAndFuture2 {
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        Future<Integer> future = threadPool.submit(new Callable<Integer>() {
            public Integer call() throws Exception {
                return new Random().nextInt(100);
            }
        });
        try {
            Thread.sleep(5000);// 可能做一些事情
            System.out.println(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
