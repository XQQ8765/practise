package com.xqq.asm.practise1;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by rxiao on 2/2/15.
 */
public class ChangeVersionAdapter extends ClassVisitor {
    public ChangeVersionAdapter(ClassVisitor cv) {
        super(Opcodes.ASM4, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        //Change the version to Opcodes.V1_2
        cv.visit(Opcodes.V1_2, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitEnd() {
        //Create a new field
        cv.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "newIntegerField", "I",
                null, new Integer(-1)).visitEnd();
        //Add a new method "compareTo"
        cv.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "compareTo",
                "(Ljava/lang/Object;)I", null, null).visitEnd();
    }
}
