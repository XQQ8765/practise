package asm.examples.compile;

import com.xqq.asm.util.ClassUtil;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by rxiao on 3/6/15.
 */
public class MyExpressionTest {
    @Test
    public void testPrintMyExression() throws IOException {
        ClassUtil.printWithTraceClassVisit("asm.examples.compile.MyExpression");
    }
}
