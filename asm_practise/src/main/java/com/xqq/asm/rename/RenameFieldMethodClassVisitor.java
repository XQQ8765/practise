package com.xqq.asm.rename;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import java.util.HashMap;
import java.util.Map;
import static org.objectweb.asm.Opcodes.ASM4;

/**
 * Created by rxiao on 3/10/15.
 */
public class RenameFieldMethodClassVisitor extends ClassVisitor{


    public RenameFieldMethodClassVisitor(ClassVisitor cv) {
        super(ASM4, cv);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, RenameMapUtil.getReplacedFieldName(name), desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, RenameMapUtil.getReplacedMethodName(name), desc, signature, exceptions);
        return new RenameMethodVisitor(mv);
    }

    private class RenameMethodVisitor extends MethodVisitor {

        public RenameMethodVisitor(MethodVisitor cv) {
            super(ASM4, cv);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            super.visitFieldInsn(opcode, owner, RenameMapUtil.getReplacedFieldName(name), desc);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            super.visitMethodInsn(opcode, owner, RenameMapUtil.getReplacedMethodName(name), desc, itf);
        }
    }



}
