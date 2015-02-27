package com.xqq.asm.practise3;

import com.xqq.asm.practise2.AddTimerAdapter;
import com.xqq.asm.util.ClassUtil;
import com.xqq.asm.util.MyClassLoader;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static junit.framework.Assert.assertTrue;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

/**
 * Created by rxiao on 2/26/15.
 */
public class AddTimeTransformerTest {
    private static String BEAN_NAME = "com.xqq.asm.practise2.C";
    private Class beanClazz;

    @Before
    public void setUp() throws IOException {
        ClassNode classNode = new ClassNode();
        ClassReader cr = new ClassReader(BEAN_NAME);
        cr.accept(classNode, ClassReader.SKIP_DEBUG);

        ClassTransformer transformer0 = new AddTimerTransformer(null);
        transformer0.transform(classNode);

        ClassWriter cw = new ClassWriter(0);
        classNode.accept(cw);

        byte[] classBytes = cw.toByteArray();
        ClassUtil.printWithTraceClassVisit(classBytes);

        //Load the class
        MyClassLoader myClassLoader = new MyClassLoader();
        beanClazz = myClassLoader.defineClass(BEAN_NAME, classBytes);
    }

    @Test
    public void testTimerField() throws Exception {
        Object beanInst = beanClazz.newInstance();

        Method method = beanClazz.getMethod("sleepAFixedTime", Long.class);
        Field timerField = beanClazz.getField("timer");

        Long sleepTimer1 = 10L;
        method.invoke(beanInst, sleepTimer1);
        assertTrue(timerField.getFloat(beanInst) >= sleepTimer1);

        Long sleepTimer2 = 100L;
        method.invoke(beanInst, sleepTimer2);
        assertTrue(timerField.getFloat(beanInst) >= (sleepTimer1 + sleepTimer2));
    }
}
