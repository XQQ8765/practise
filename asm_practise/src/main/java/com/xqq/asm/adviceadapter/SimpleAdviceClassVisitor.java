package com.xqq.asm.adviceadapter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Created by rxiao on 3/31/15.
 */
public class SimpleAdviceClassVisitor extends ClassVisitor{
    private int api;
    public SimpleAdviceClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
        this.api = api;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new SimpleAdviceAdapter(api, mv, access, name, desc);
    }
}
