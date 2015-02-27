package com.xqq.asm.practise1;

import com.xqq.asm.util.ClassUtil;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Created by rxiao on 2/27/15.
 */
public class MyClassLoaderTest {

    @Test
    public void testWriteClass() {
        byte[] classBytes = new MyClassWriter().writeClass();

        ClassUtil.printWithTraceClassVisit(classBytes);
    }

    @Test
    public void testWriteClassWithTreeAPI() {
        byte[] classBytes = new MyClassWriter().writeClassWithTreeAPI();

        ClassUtil.printWithTraceClassVisit(classBytes);
    }
}
