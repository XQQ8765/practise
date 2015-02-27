package com.xqq.asm.practise3;

import com.xqq.asm.practise1.MyClassWriter;
import com.xqq.asm.util.ClassUtil;
import com.xqq.asm.util.MyClassLoader;
import static org.junit.Assert.*;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by rxiao on 2/27/15.
 */
public class ClassTransformTest {

    @Test
    public void testTransform() throws Exception {
        final String BEAN_NAME = "com.xqq.asm.practise2.Bean";
        ClassNode classNode = new ClassNode();
        ClassReader cr = new ClassReader(BEAN_NAME);
        cr.accept(classNode, ClassReader.SKIP_DEBUG);

        AddFieldTransformer transformer0 = new AddFieldTransformer(null, ACC_PUBLIC, "testField", "I");
        AddFieldTransformer transformer1 = new AddFieldTransformer(transformer0, ACC_PUBLIC, "f", "I");
        RemoveMethodTransformer transformer12 = new RemoveMethodTransformer(transformer1, "setF", "(I)V");
        transformer12.transform(classNode);

        ClassWriter cw = new ClassWriter(0);
        classNode.accept(cw);

        byte[] classBytes = cw.toByteArray();
        ClassUtil.printWithTraceClassVisit(classBytes);

        //Load the class
        MyClassLoader myClassLoader = new MyClassLoader();
        Class beanClazz = myClassLoader.defineClass(BEAN_NAME, classBytes);

        Field testField = beanClazz.getField("testField");
        assertEquals("testField", testField.getName());

        try {
            beanClazz.getMethod("setF", Long.class);
            fail();
        } catch (NoSuchMethodException e) {}
    }
}
