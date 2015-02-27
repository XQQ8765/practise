package com.xqq.asm.practise1;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.CheckClassAdapter;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by rxiao on 1/30/15.
 */
public class MyClassWriter {

    public byte[] writeClass() {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
                "org/xqq/test/Comparable",
                null,
                "java/lang/Object",
                null//new String[]{"org/xqq/test/Measurable"}
        );
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

    public byte[] writeClassWithTreeAPI() {
        ClassNode cn = new ClassNode();
        cn.version = V1_5;
        cn.access = ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE;
        cn.name = "org/xqq/test/Comparable";
        cn.superName = "java/lang/Object";
        //cn.interfaces.add("org/xqq/test/Measurable");

        cn.fields.add(new FieldNode(ACC_PUBLIC + ACC_FINAL + ACC_STATIC,  "LESS", "I",
                null, new Integer(-1)));
        cn.fields.add(new FieldNode(ACC_PUBLIC + ACC_FINAL + ACC_STATIC,  "EQUAL", "I",
                null, new Integer(0)));
        cn.fields.add(new FieldNode(ACC_PUBLIC + ACC_FINAL + ACC_STATIC,  "GREATER", "I",
                null, new Integer(1)));

        cn.methods.add(new MethodNode(ACC_PUBLIC + ACC_ABSTRACT, "compareTo",
                "(Ljava/lang/Object;)I", null, null));

        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
