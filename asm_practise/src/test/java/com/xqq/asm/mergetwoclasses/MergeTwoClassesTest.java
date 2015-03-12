package com.xqq.asm.mergetwoclasses;

import com.xqq.asm.rename.C;
import com.xqq.asm.rename.RenameFieldMethodClassVisitor;
import com.xqq.asm.util.ClassUtil;
import com.xqq.asm.util.MyClassLoader;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by rxiao on 2/26/15.
 */
public class MergeTwoClassesTest {

    @Test
    public void testMergeClass() throws Exception {
        Class clazz1 = C1.class;
        Class clazz2 = C2.class;
        byte[] classBytes = MergeClassUtil.mergeClass(clazz1, clazz2);
        System.out.println("----------------------------------Class After Merge---------------------------------------------");
        ClassUtil.printWithTraceClassVisit(classBytes);

        //Load the class
        MyClassLoader myClassLoader = new MyClassLoader();
        Class beanClazz = myClassLoader.defineClass(clazz1.getName(), classBytes);
        assertEquals(clazz1.getName(), beanClazz.getName());
    }

    @Test
    public void testMergeInterfaces() throws IOException {
        Class clazz1 = C1.class;
        Class clazz2 = Opcodes.class;//clazz2 is an interface
        byte[] classBytes = MergeClassUtil.mergeClass(clazz1, clazz2);
        System.out.println("----------------------------------Class After Merge---------------------------------------------");
        ClassUtil.printWithTraceClassVisit(classBytes);
        //Load the class
        MyClassLoader myClassLoader = new MyClassLoader();
        Class beanClazz = myClassLoader.defineClass(clazz1.getName(), classBytes);
        Class[] interfaces = beanClazz.getInterfaces();

        //Test the merged class has implement the interface
        boolean containInterface = false;
        for (Class ifClazz : interfaces) {
            if (ifClazz.getName().equals(clazz2.getName())) {
                containInterface = true;
            }
        }
        assertTrue(containInterface);

        clazz1 = Opcodes.class;//clazz1 is an interface
        clazz2 = C1.class;
        try {
            classBytes = MergeClassUtil.mergeClass(clazz1, clazz2);
            fail();
        } catch (IllegalStateException e) {
        }
    }
}
