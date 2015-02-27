package com.xqq.asm.practise2;

import com.xqq.asm.util.MyClassLoader;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static junit.framework.Assert.assertTrue;

/**
 * Created by rxiao on 2/26/15.
 */
public class AddTimeAdapterTest {
    private static String BEAN_NAME = "com.xqq.asm.practise2.C";
    private Class beanClazz;

    @Before
    public void setUp() throws IOException {
        ClassReader classReader = new ClassReader(BEAN_NAME);
        ClassWriter classWriter = new ClassWriter(classReader, 0);
        AddTimerAdapter addTimerAdapter = new AddTimerAdapter(classWriter);
        classReader.accept(addTimerAdapter, 0);

        byte[] classBytes = classWriter.toByteArray();

        //Print the instractions for the class bytes
        ClassVisitor classPrinter = new TraceClassVisitor(new PrintWriter(System.out));
        classReader = new ClassReader(classBytes);
        classReader.accept(classPrinter, 0);

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
