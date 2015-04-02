package com.xqq.asm.adviceadapter;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by rxiao on 4/1/15.
 */
public class SimpleAdviceAdapter extends AdviceAdapter {
    @Override
    public void visitCode() {
        System.out.println("### SimpleAdviceAdapter: visitCode()");
        super.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        System.out.println("### SimpleAdviceAdapter: visitInsn(): opcode:"+opcode);
        super.visitInsn(opcode);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        System.out.println("### SimpleAdviceAdapter: visitLineNumber(): line:"+line+", start:"+start);
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        System.out.println("### SimpleAdviceAdapter: visitFieldInsn():opcode:"+opcode
                +", owner:"+owner+", name:"+name+", desc:"+desc);
        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        System.out.println("### SimpleAdviceAdapter: visitMethodInsn():opcode:"+opcode
                +", owner:"+owner+", name:"+name+", desc:"+desc);
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        System.out.println("### SimpleAdviceAdapter: visitTryCatchBlock():start:"+start
                +", end:"+end+", handler:"+handler+", type:"+type);
        super.visitTryCatchBlock(start, end, handler, type);
    }

    protected SimpleAdviceAdapter(int api, MethodVisitor mv, int access, String name, String desc) {
        super(api, mv, access, name, desc);
    }
}
