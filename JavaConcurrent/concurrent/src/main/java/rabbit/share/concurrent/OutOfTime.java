package rabbit.share.concurrent;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 Class Illustrating Confusing Timer Behavior.
 */
public class OutOfTime {
    public static void main(String[] args) throws Exception {
        /*Timer timer = new Timer();
        timer.schedule(new ThrowTask(), 1);
        Thread.sleep(1);
        timer.schedule(new ThrowTask(), 1);
        Thread.sleep(5);    */
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
        scheduledExecutorService.scheduleAtFixedRate(new ThrowExceptionRunnable(), 0, 3, TimeUnit.SECONDS);

        Thread.sleep(100 * 1000L);
    }

    static class ThrowTask extends TimerTask {
        public void run() {
            System.out.println("Run in Timer. Date:"+ new Date());
            throw new RuntimeException();
        }
    }

    static class ThrowExceptionRunnable implements Runnable {
        public void run() {
            System.out.println("Run in ScheduledExecutorService. Date:"+ new Date());
            //throw new RuntimeException();
        }
    }
}
