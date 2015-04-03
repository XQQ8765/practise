package com.xiaoqq.practise.threadmonitor;
import com.xiaoqq.practise.threadmonitor.util.MonitorUtil;
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
        boolean b = MonitorUtil.isAssignableFrom("java/lang/Object", "java/lang/Thread", this.getClass().getClassLoader());
        assertTrue(b);

        b = MonitorUtil.isAssignableFrom("java/lang/Thread", "com/xiaoqq/practise/threadsamplecode/WaitExample1$ThreadB", this.getClass().getClassLoader());
        assertTrue(b);
    }
}
