package com.xqq.asm.practise2;

import com.xqq.asm.util.ClassUtil;
import com.xqq.asm.util.MyClassLoader;
import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import org.junit.Before;
/**
 * Created by rxiao on 2/10/15.
 */
public class BeanWriterWithCoreAPITest {
    private Class beanClazz;

    @Before
    public void setUp() throws Exception{
        byte[] classBytes = new BeanWriterWithCoreAPI().writeClass();
        //File classFile = new File("d:\\workspace\\tmp\\asm\\org\\xqq\\test\\Bean.class");
        //File classFile = new File("d:\\workspace\\practise\\asm_practise\\src\\main\\java\\com\\xqq\\asm\\practise2\\Bean.class");
        //byte[] classBytes = FileUtils.readFileToByteArray(classFile);

        //Print the instractions for the class bytes
        ClassUtil.printWithTraceClassVisit(classBytes);

        //Load the class
        MyClassLoader myClassLoader = new MyClassLoader();
        beanClazz = myClassLoader.defineClass(BeanWriterWithCoreAPI.BEAN_DOT_NAME, classBytes);
    }

    @Test
    public void testGetAndSetMethod() throws Exception{
        Integer f = 10;
        Object beanInst = beanClazz.newInstance();
        Method setFMethod = beanClazz.getMethod("setF", int.class);
        setFMethod.invoke(beanInst, f);
        Method getFMethod = beanClazz.getMethod("getF", null);
        Integer fieldV = (Integer) getFMethod.invoke(beanInst, null);
        Assert.assertEquals("f and fieldV should be equal", f, fieldV);
    }

    @Test
    public void testCheckAndSetF() throws Exception {
        Object beanInst = beanClazz.newInstance();
        Integer f = 6;
        Method checkAndSetFMethod = beanClazz.getMethod("checkAndSetF", int.class);
        Method getFMethod = beanClazz.getMethod("getF", null);

        checkAndSetFMethod.invoke(beanInst, 6);
        Integer fieldV = (Integer) getFMethod.invoke(beanInst, null);
        Assert.assertEquals("f and fieldV should be equal", f, fieldV);

        try {
            //Will throw IllegalArgumentException
            checkAndSetFMethod.invoke(beanInst, 100);
            Assert.fail();
        } catch (Exception e) {
            Throwable exception = e.getCause();
            Assert.assertTrue(exception instanceof IllegalArgumentException);
            Assert.assertEquals("Only 1<=f<=10 is allowed.", exception.getMessage());
        }

        try {
            //Will throw IllegalArgumentException
            checkAndSetFMethod.invoke(beanInst, -1);
            Assert.fail();
        } catch (Exception e) {
            Throwable exception = e.getCause();
            Assert.assertTrue(exception instanceof IllegalArgumentException);
            Assert.assertEquals("Only 1<=f<=10 is allowed.", exception.getMessage());
        }
    }


}
