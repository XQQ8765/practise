package com.xiaoqq.practise.threadmonitor;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Created by rxiao on 3/31/15.
 */
public class MonitorUtilTest {
    @Test
    public void testGetObjectId() {
        String str = "abc";
        System.out.println(MonitorUtil.getObjectId(str));

        Object obj = new Thread("thread-1");
        System.out.println(MonitorUtil.getObjectId(obj));

    }

    @Test
    public void testIsAssignableFrom() {
        boolean b = MonitorUtil.isAssignableFrom("java/lang/Object", "java/lang/Thread");
        assertTrue(b);

        b = MonitorUtil.isAssignableFrom("java/lang/Thread", "com/xiaoqq/practise/threadsamplecode/WaitExample1$ThreadB");
        assertTrue(b);
    }
}
