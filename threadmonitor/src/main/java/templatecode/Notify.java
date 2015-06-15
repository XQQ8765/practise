package templatecode;

import com.xiaoqq.practise.threadmonitor.relationship.model.CodePosition;
import com.xiaoqq.practise.threadmonitor.relationship.model.EventListener;

/**
 * Created by rxiao on 6/15/15.
 */
public class Notify {
    private static void testNotify() {
        Object obj = new Object();

        CodePosition codePosition = new CodePosition();
        codePosition.setClassName("XXX_className");
        codePosition.setMethodName("XXX_methodName");
        codePosition.setMethodDesc("XXX_methodDesc");
        Object localObject1 = obj;

        EventListener.beforeWait(localObject1, codePosition);
        localObject1.notify();
    }
}
