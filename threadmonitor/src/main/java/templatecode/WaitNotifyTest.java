package templatecode;

import com.xiaoqq.practise.threadmonitor.relationship.model.CodePosition;
import com.xiaoqq.practise.threadmonitor.relationship.model.EventListener;

public class WaitNotifyTest {
    public static void main(String[] args) {
        test();
    }

    private static void test() {
        try {
            Object obj = new Object();
            Object localObject1 = obj;
            CodePosition localCodePosition = new CodePosition();
            localCodePosition.setClassName("com/xiaoqq/practise/threadsamplecode/wait/WaitNotifyTest");
            localCodePosition.setMethodName("test");
            localCodePosition.setMethodDesc("()V");
            EventListener.beforeWait(localObject1, localCodePosition);
            localObject1.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

