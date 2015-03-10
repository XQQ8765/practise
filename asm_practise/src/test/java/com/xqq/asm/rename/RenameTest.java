package com.xqq.asm.rename;

import com.xqq.asm.introduceInterface.AddOperationClassTransformer;
import com.xqq.asm.introduceInterface.SubClassVisitor;
import com.xqq.asm.util.ClassUtil;
import com.xqq.asm.util.MyClassLoader;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by rxiao on 2/26/15.
 */
public class RenameTest {
    private Class beanClazz;
    private Class innerBeanClazz;

    @Before
    public void setUp() throws IOException {
        System.out.println("----------------------------------C.class---------------------------------------------");
        byte[] classBytes = transformClass(C.class);
        ClassUtil.printWithTraceClassVisit(classBytes);
        System.out.println("----------------------------------C.InnerC.class---------------------------------------------");
        byte[] innerClassBytes = transformClass(C.InnerC.class);
        ClassUtil.printWithTraceClassVisit(innerClassBytes);

        //Load the class
        MyClassLoader myClassLoader = new MyClassLoader();
        beanClazz = myClassLoader.defineClass("com.xqq.asm.rename.C", classBytes);
        innerBeanClazz = myClassLoader.defineClass("com.xqq.asm.rename.C$InnerC", innerClassBytes);
    }

    private byte[] transformClass(Class clazz) throws IOException {
        ClassNode classNode = new ClassNode();
        ClassVisitor cv = new RenameFieldMethodClassVisitor(classNode);
        ClassReader cr = new ClassReader(clazz.getName());
        cr.accept(cv, ClassReader.SKIP_DEBUG);

        ClassWriter cw = new ClassWriter(0);
        classNode.accept(cw);

        return cw.toByteArray();
    }

    @Test
    public void testRenameOuterClass() throws Exception {
        int i0 = 1;

        Constructor cm = beanClazz.getConstructor(int.class);
        Object beanInst = cm.newInstance(i0);

        Method getKMethod = beanClazz.getMethod("getK");
        int r = (Integer) getKMethod.invoke(beanInst);
        assertEquals(r, i0);

        int i1 = 5;
        Method addMethod = beanClazz.getMethod("add", int.class);
        addMethod.invoke(beanInst, i1);
        r = (Integer) getKMethod.invoke(beanInst);
        assertEquals(r, i0 + i1);

        i1 = 10;
        Method setKMethod = beanClazz.getMethod("setK", int.class);
        setKMethod.invoke(beanInst, i1);
        r = (Integer) getKMethod.invoke(beanInst);
        assertEquals(r, i1);

        Method greaterThanMethod = beanClazz.getMethod("greaterThan", int.class);
        i1 = 8;
        Boolean b = (Boolean) greaterThanMethod.invoke(beanInst, i1);
        assertTrue(b);
        i1 = 11;
        b = (Boolean) greaterThanMethod.invoke(beanInst, i1);
        assertTrue(!b);

        Method betweenMethod = beanClazz.getMethod("between", int.class, int.class);
        i0 = 1;
        i1 = 11;
        b = (Boolean) betweenMethod.invoke(beanInst, i0, i1);
        assertTrue(b);

        i0 = 10;
        i1 = 11;
        b = (Boolean) betweenMethod.invoke(beanInst, i0, i1);
        assertTrue(!b);
    }

    @Test
    public void testRenameInnerClass() throws Exception {
        Method lessThan100Method = innerBeanClazz.getMethod("lessThan100", int.class);

        int i0 = 10;
        Boolean b = (Boolean) lessThan100Method.invoke(null, i0);
        assertTrue(b);

        i0 = 101;
        b = (Boolean) lessThan100Method.invoke(null, i0);
        assertTrue(!b);
    }
}
