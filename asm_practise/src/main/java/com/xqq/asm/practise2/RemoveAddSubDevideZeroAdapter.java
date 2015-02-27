package com.xqq.asm.practise2;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by rxiao on 2/26/15.
 */
public class RemoveAddSubDevideZeroAdapter extends ClassVisitor{

    public RemoveAddSubDevideZeroAdapter(ClassVisitor cv) {
        super(ASM4, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null) {
            mv = new RemoveAddSubDevideZeroMethodAdapter(mv);
        }
        return mv;
    }

    class RemoveAddSubDevideZeroMethodAdapter extends MethodVisitor {
        private boolean seenConstant0;
        private final List<Integer> ignoreInsts = Arrays.asList(IADD, ISUB, IDIV, FADD, FSUB, FDIV, DADD, DSUB, DDIV);

        RemoveAddSubDevideZeroMethodAdapter(MethodVisitor mv) {
            super(ASM4, mv);
            seenConstant0 = false;
        }

        @Override
        public void visitInsn(int opcode) {
            if (seenConstant0) {
                seenConstant0 = false;
                if(ignoreInsts.contains(opcode)) {
                    return;//Do not process any more, the two instructions will be deleted.
                } else {
                    mv.visitInsn(ICONST_0);//Process ICONST_0
                }
            }
            if (opcode == ICONST_0) {
                seenConstant0 = true;
                return;//Do not process ICONST_0, process it with other instructions later together.
            }
            super.visitInsn(opcode);
        }
    }

}
