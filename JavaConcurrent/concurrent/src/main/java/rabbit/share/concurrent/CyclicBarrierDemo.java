package rabbit.share.concurrent;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-6-10
 * Time: 下午10:40
 * To change this template use File | Settings | File Templates.
 */
public class CyclicBarrierDemo {
    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool();
        final CyclicBarrier cb = new CyclicBarrier(3);
        for (int i = 0; i < 3; i++) {
            Runnable runnable = new Runnable() {
                public void run() {
                    try {
                        Thread.sleep((long) (Math.random() * 10000));
                        System.out.println("线程"
                                + Thread.currentThread().getName()
                                + "即将到达集合地点1，当前已有"
                                + (cb.getNumberWaiting() + 1)
                                + "个已经到达，"
                                + (cb.getNumberWaiting() == 2 ? "都到齐了，继续走啊"
                                : "正在等候"));
                        cb.await();//只有三个线程都到齐了才往下走

                        Thread.sleep((long) (Math.random() * 10000));
                        System.out.println("线程"
                                + Thread.currentThread().getName()
                                + "即将到达集合地点2，当前已有"
                                + (cb.getNumberWaiting() + 1)
                                + "个已经到达，"
                                + (cb.getNumberWaiting() == 2 ? "都到齐了，继续走啊"
                                : "正在等候"));

                        cb.await();
                        Thread.sleep((long) (Math.random() * 10000));
                        System.out.println("线程"
                                + Thread.currentThread().getName()
                                + "即将到达集合地点3，当前已有"
                                + (cb.getNumberWaiting() + 1)
                                + "个已经到达，"
                                + (cb.getNumberWaiting() == 2 ? "都到齐了，继续走啊"
                                : "正在等候"));

                        cb.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            service.execute(runnable);
        }
        service.shutdown();
    }
}
