package com.xqq.asm.mergetwoclasses;

import com.xqq.asm.util.ClassUtil;
import com.xqq.asm.util.MyClassLoader;
import org.junit.Test;
import org.objectweb.asm.Opcodes;

import java.io.IOException;

import static junit.framework.Assert.*;

/**
 * Created by rxiao on 2/26/15.
 */
public class MergeTwoClasses2Test {
    /*
    @Test
    public void testMergeClass() throws Exception {
        Class clazz1 = C1.class;
        Class clazz2 = C2.class;
        byte[] classBytes = MergeClassUtil.mergeClass2(clazz1, clazz2);
        System.out.println("----------------------------------Class After Merge---------------------------------------------");
        ClassUtil.printWithTraceClassVisit(classBytes);

        //Load the class
        MyClassLoader myClassLoader = new MyClassLoader();
        Class beanClazz = myClassLoader.defineClass(clazz1.getName(), classBytes);
        assertEquals(clazz1.getName(), beanClazz.getName());
    }
    */
}
