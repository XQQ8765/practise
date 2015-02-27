package com.xqq.asm.practise2;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AnalyzerAdapter;
import static org.objectweb.asm.Opcodes.*;

/**
 * Created by rxiao on 2/26/15.
 */
public class AddTimerAdapter extends ClassVisitor{
    private String owner;
    private boolean isInterface;

    public AddTimerAdapter(ClassVisitor cv) {
        super(ASM4, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = name;
        this.isInterface = ((access & ACC_INTERFACE) != 0);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        //Set "AddTimerMethodAdapter" to delegate all the method calls except constructs
        if (!isInterface && mv != null && !name.equals("<init>")) {
            //mv = new AddTimerMethodAdapter(mv);
            mv = new AddTimerMethodAdapter2(owner, access, name, desc, mv);
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        if (!isInterface) {
            //Add field: "public static Long timer;"
            FieldVisitor fv = cv.visitField(ACC_PUBLIC + ACC_STATIC, "timer", "J", null, null);
            if (fv != null) {
                fv.visitEnd();
            }
        }
        super.visitEnd();
    }

    private class AddTimerMethodAdapter extends MethodVisitor {
        AddTimerMethodAdapter(MethodVisitor mv) {
            super(ASM4, mv);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            //Generate code: timer -= System.currentTimeMillis();
            mv.visitFieldInsn(GETSTATIC, owner, "timer", "J");
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitInsn(LSUB);
            mv.visitFieldInsn(PUTSTATIC, owner, "timer", "J");
        }

        @Override
        public void visitInsn(int opcode) {
            //Generate code at the end of method: timer += System.currentTimeMillis();
            if (opcode >= IRETURN && opcode <= RETURN || opcode == ATHROW) {
                mv.visitFieldInsn(GETSTATIC, owner, "timer", "J");
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                mv.visitInsn(LADD);
                mv.visitFieldInsn(PUTSTATIC, owner, "timer", "J");
            }
            super.visitInsn(opcode);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            //The instructions we add push two long values, and therefore require four slots on the operand stack.
            super.visitMaxs(maxStack + 4, maxLocals);
        }
    }

    private class AddTimerMethodAdapter2 extends AnalyzerAdapter {
        private AddTimerMethodAdapter2(String owner, int access, String name, String desc, MethodVisitor mv) {
            super(ASM4, owner, access, name, desc, mv);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            //Generate code: timer -= System.currentTimeMillis();
            super.visitFieldInsn(GETSTATIC, owner, "timer", "J");
            super.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            super.visitInsn(LSUB);
            super.visitFieldInsn(PUTSTATIC, owner, "timer", "J");
        }

        @Override
        public void visitInsn(int opcode) {
            //Generate code at the end of method: timer += System.currentTimeMillis();
            if (opcode >= IRETURN && opcode <= RETURN || opcode == ATHROW) {
                super.visitFieldInsn(GETSTATIC, owner, "timer", "J");
                super.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                super.visitInsn(LADD);
                super.visitFieldInsn(PUTSTATIC, owner, "timer", "J");
            }
            super.visitInsn(opcode);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            //The instructions we add push two long values, and therefore require four slots on the operand stack.
            super.visitMaxs(maxStack + 4, maxLocals);
        }
    }
}
