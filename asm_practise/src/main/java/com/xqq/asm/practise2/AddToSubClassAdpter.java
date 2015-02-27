package com.xqq.asm.practise2;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

/**
 * Replace the "Add" insn to "Sub"
 */
public class AddToSubClassAdpter extends ClassVisitor{
    public AddToSubClassAdpter(ClassVisitor cv) {
        super(ASM4, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null) {
            mv = new AddToSubMethodAdpter(mv);
        }
        return mv;
    }

    /**
     * Replace the "Add" insn to "Sub" in method
     */
    private static class AddToSubMethodAdpter extends MethodVisitor{
        public AddToSubMethodAdpter(MethodVisitor mv) {
            super(ASM4, mv);
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode == IADD) {
                opcode = ISUB;
            } else if (opcode == FADD) {
                opcode = FSUB;
            } else if (opcode == DADD) {
                opcode = DSUB;
            }
            super.visitInsn(opcode);
        }
    }
}
