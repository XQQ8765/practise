package com.xqq.asm.introduceInterface;

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
public class IntroduceInterfaceTest {
    private static String BEAN_NAME = "com.xqq.asm.introduceInterface.C";
    private Class beanClazz;

    @Before
    public void setUp() throws IOException {
        ClassNode classNode = new ClassNode();
        ClassVisitor transformer0 = new AddOperationClassTransformer(classNode);
        ClassVisitor cv = new SubClassVisitor(transformer0);
        ClassReader cr = new ClassReader(BEAN_NAME);
        cr.accept(cv, ClassReader.SKIP_DEBUG);

        ClassWriter cw = new ClassWriter(0);
        classNode.accept(cw);

        byte[] classBytes = cw.toByteArray();
        ClassUtil.printWithTraceClassVisit(classBytes);

        //Load the class
        MyClassLoader myClassLoader = new MyClassLoader();
        String className = StringUtils.replace(classNode.name, "/", ".");
        beanClazz = myClassLoader.defineClass(className, classBytes);
    }

    @Test
    public void testAddSubMethod() throws Exception {
        int i0 = 1;

        Constructor cm = beanClazz.getConstructor(int.class);
        Object beanInst = cm.newInstance(i0);

        int i1 = 2;
        Method method = beanClazz.getMethod("add", int.class);
        method.invoke(beanInst, i1);

        Method getVMethod = beanClazz.getMethod("getK");
        int r = (Integer) getVMethod.invoke(beanInst);
        assertEquals(r, i0 + i1);

        Method subMethod = beanClazz.getMethod("sub", int.class);
        subMethod.invoke(beanInst, i1);
        r = (Integer) getVMethod.invoke(beanInst);
        assertEquals(r, i0);
    }
}
