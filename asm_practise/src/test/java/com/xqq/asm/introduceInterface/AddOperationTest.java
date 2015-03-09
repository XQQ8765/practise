package com.xqq.asm.introduceInterface;

import com.xqq.asm.practise3.AddTimerTransformer;
import com.xqq.asm.practise3.ClassTransformer;
import com.xqq.asm.util.ClassUtil;
import com.xqq.asm.util.MyClassLoader;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by rxiao on 2/26/15.
 */
public class AddOperationTest {
    private static String BEAN_NAME = "com.xqq.asm.introduceInterface.C";
    private Class beanClazz;

    @Before
    public void setUp() throws IOException {
        ClassNode classNode = new ClassNode();
        ClassReader cr = new ClassReader(BEAN_NAME);
        cr.accept(classNode, ClassReader.SKIP_DEBUG);

        AddOperationClassTransformer transformer0 = new AddOperationClassTransformer();
        transformer0.transform(classNode);

        ClassWriter cw = new ClassWriter(0);
        classNode.accept(cw);

        byte[] classBytes = cw.toByteArray();
        ClassUtil.printWithTraceClassVisit(classBytes);

        //Load the class
        MyClassLoader myClassLoader = new MyClassLoader();
        //beanClazz = myClassLoader.defineClass(classNode.name, classBytes);
        beanClazz = myClassLoader.defineClass("com.xqq.asm.introduceInterface.C_Sub", classBytes);
    }

    @Test
    public void testTimerField() throws Exception {
        int i0 = 1;

        Constructor cm = beanClazz.getConstructor(int.class);
        Object beanInst = cm.newInstance(i0);

        int i1 = 2;
        Method method = beanClazz.getMethod("add", int.class);
        method.invoke(beanInst, i1);

        Method getVMethod = beanClazz.getMethod("getV");
        int r = (Integer) getVMethod.invoke(beanInst);
        assertEquals(r, i0 + i1);
        /*
        Field timerField = beanClazz.getField("timer");

        Long sleepTimer1 = 10L;
        method.invoke(beanInst, sleepTimer1);
        assertTrue(timerField.getFloat(beanInst) >= sleepTimer1);

        Long sleepTimer2 = 100L;
        method.invoke(beanInst, sleepTimer2);
        assertTrue(timerField.getFloat(beanInst) >= (sleepTimer1 + sleepTimer2));
        */
    }
}
