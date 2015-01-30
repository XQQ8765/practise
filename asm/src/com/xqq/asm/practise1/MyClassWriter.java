package com.xqq.asm.practise1;

import org.objectweb.asm.ClassWriter;
import static org.objectweb.asm.Opcodes.*;

/**
 * Created by rxiao on 1/30/15.
 */
public class MyClassWriter {

    public byte[] writeClass() {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
                    "org/xqq/test/Comparable", null, "java/lang/Object",
                    new String[]{"org/xqq/test/Measurable"});
        //Create static fields
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "LESS", "I",
                null, new Integer(-1)).visitEnd();
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "EQUAL", "I",
                null, new Integer(0)).visitEnd();
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "GREATER", "I",
                null, new Integer(1)).visitEnd();
        //Create method
        cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "compareTo",
                "(Ljava/lang/Object;)I", null, null).visitEnd();
        cw.visitEnd();

        return cw.toByteArray();
    }
}
