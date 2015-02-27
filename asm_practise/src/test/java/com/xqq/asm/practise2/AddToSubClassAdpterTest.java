package com.xqq.asm.practise2;

import static junit.framework.Assert.*;
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

/**
 * Created by rxiao on 2/26/15.
 */
public class AddToSubClassAdpterTest {
    private static String BEAN_NAME = "com.xqq.asm.practise2.Calculate";
    private Class beanClazz;

    @Before
    public void setUp() throws IOException {
        ClassReader classReader = new ClassReader(BEAN_NAME);
        ClassWriter classWriter = new ClassWriter(classReader, 0);
        AddToSubClassAdpter addToSubClassAdpter = new AddToSubClassAdpter(classWriter);
        classReader.accept(addToSubClassAdpter, 0);

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
    public void testAddToSub() throws Exception{
        Object beanInst = beanClazz.newInstance();

        Integer i0 = 10;
        Integer i1 = 2;
        Method method = beanClazz.getMethod("add", int.class, int.class);
        int i = (Integer) method.invoke(beanInst, i0, i1);
        assertEquals(i0-i1, i);

        //Instance method
        method = beanClazz.getMethod("addInst", int.class, int.class);
        i = (Integer) method.invoke(beanInst, i0, i1);
        assertEquals(i0-i1, i);

        Float f0 = 10f;
        Float f1 = 2f;
        method = beanClazz.getMethod("add", float.class, float.class);
        float f = (Float) method.invoke(beanInst, f0, f1);
        assertEquals(f0-f1, f);

        Double d0 = 11d;
        Double d1 = 2d;
        method = beanClazz.getMethod("add", double.class, double.class);
        double d = (Double) method.invoke(beanInst, d0, d1);
        assertEquals(d0-d1, d);
    }
}
