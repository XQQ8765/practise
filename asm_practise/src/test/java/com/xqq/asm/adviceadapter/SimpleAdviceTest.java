package com.xqq.asm.adviceadapter;

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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;

/**
 * Created by rxiao on 2/26/15.
 */
public class SimpleAdviceTest {
    private Class beanClazz;
    /*
    @Before
    public void setUp() throws IOException {
        ClassReader classReader = new ClassReader(MyTestThread.class.getName());
        ClassVisitor cv = new SimpleAdviceClassVisitor(Opcodes.ASM5, null);
        classReader.accept(cv, ClassReader.SKIP_DEBUG);
    }
     */

    @Test
    public void testSimpleAdvice() throws Exception {
        ClassReader classReader = new ClassReader(MyTestThread.class.getName());
        ClassWriter cw = new ClassWriter(Opcodes.ASM5);
        ClassVisitor cv = new SimpleAdviceClassVisitor(Opcodes.ASM5, cw);
        classReader.accept(cv, ClassReader.EXPAND_FRAMES);
    }
}
