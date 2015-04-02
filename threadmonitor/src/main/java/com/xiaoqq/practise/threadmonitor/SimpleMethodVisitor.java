package com.xiaoqq.practise.threadmonitor;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * Created by rxiao on 4/1/15.
 */
public class SimpleMethodVisitor extends MethodVisitor {
    @Override
    public void visitCode() {
        System.out.println("### SimpleMethodVisitor: visitCode()");
        super.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        System.out.println("### SimpleMethodVisitor: visitInsn(): opcode:"+opcode);
        super.visitInsn(opcode);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        System.out.println("### SimpleMethodVisitor: visitLineNumber(): line:"+line+", start:"+start);
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        System.out.println("### SimpleMethodVisitor: visitFieldInsn():opcode:"+opcode
                +", owner:"+owner+", name:"+name+", desc:"+desc);
        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        System.out.println("### SimpleMethodVisitor: visitMethodInsn():opcode:"+opcode
                +", owner:"+owner+", name:"+name+", desc:"+desc);
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        System.out.println("### SimpleMethodVisitor: visitTryCatchBlock():start:"+start
                +", end:"+end+", handler:"+handler+", type:"+type);
        super.visitTryCatchBlock(start, end, handler, type);
    }

    public SimpleMethodVisitor(int api, MethodVisitor mv) {
        super(api, mv);
    }
}
