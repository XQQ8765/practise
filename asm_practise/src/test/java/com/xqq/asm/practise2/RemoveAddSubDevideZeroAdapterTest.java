package com.xqq.asm.practise2;

import com.xqq.asm.util.ClassUtil;
import com.xqq.asm.util.MyClassLoader;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Created by rxiao on 2/26/15.
 */
public class RemoveAddSubDevideZeroAdapterTest {
    private static String BEAN_NAME = "com.xqq.asm.practise2.ZeroCalculate";
    private Class beanClazz;

    @Before
    public void setUp() throws IOException {
        System.out.println("-------------------------------------Before modify byte code:");
        ClassUtil.printWithTraceClassVisit(BEAN_NAME);

        ClassReader classReader = new ClassReader(BEAN_NAME);
        ClassWriter classWriter = new ClassWriter(classReader, 0);
        RemoveAddSubDevideZeroAdapter removeAddSubDevideZeroAdapter = new RemoveAddSubDevideZeroAdapter(classWriter);
        classReader.accept(removeAddSubDevideZeroAdapter, 0);

        byte[] classBytes = classWriter.toByteArray();

        System.out.println("-------------------------------------After modify byte code:");
        //Print the instractions for the class bytes
        ClassUtil.printWithTraceClassVisit(classBytes);

        //Load the class
        MyClassLoader myClassLoader = new MyClassLoader();
        beanClazz = myClassLoader.defineClass(BEAN_NAME, classBytes);
    }

    @Test
    public void testCalculate0() throws Exception{
        try {
            ZeroCalculate.calculate0();
            fail();
        }catch (ArithmeticException e) {}

        Object beanInst = beanClazz.newInstance();

        Method method = beanClazz.getMethod("calculate0");
        int result = (Integer) method.invoke(beanInst);
        assertEquals(result, 100);
    }
}
