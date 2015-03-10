package com.xqq.asm.introduceInterface;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by xiaoqq on 15-3-9.
 */

public class AddOperationClassTransformer extends ClassVisitor{
    public static final String BEAN = "com/xqq/asm/introduceInterface/C";

    public AddOperationClassTransformer(ClassNode cn) {
        super(ASM4, cn);
    }

    @Override
    public void visitEnd() {
        transform((ClassNode) cv);
        super.visitEnd();
    }

    public void transform(ClassNode cn) {
        MethodNode mn = createConstructMethodNode();
        cn.methods.add(mn);

        cn.interfaces.add("com/xqq/asm/introduceInterface/IAdd");
        mn = createAddMethodNode();
        cn.methods.add(mn);

        cn.interfaces.add("com/xqq/asm/introduceInterface/ISub");
        mn = createSubMethodNode();
        cn.methods.add(mn);

    }

    private MethodNode createConstructMethodNode() {
        MethodNode mn = new MethodNode(ACC_PUBLIC, "<init>","(I)V", null, null);
        InsnList il = mn.instructions;
        il.add(new VarInsnNode(ALOAD, 0));
        il.add(new VarInsnNode(ILOAD, 1));

        il.add(new MethodInsnNode(INVOKESPECIAL, BEAN, "<init>", "(I)V", false));
        il.add(new InsnNode(RETURN));

        mn.maxLocals = 2;
        mn.maxStack = 2;
        return mn;
    }

    private MethodNode createAddMethodNode() {
        MethodNode mn = new MethodNode(ACC_PUBLIC, "add","(I)I", null, null);
        InsnList il = mn.instructions;
        il.add(new VarInsnNode(ALOAD, 0));
        il.add(new MethodInsnNode(INVOKEVIRTUAL, BEAN, "getV", "()I", false));
        il.add(new VarInsnNode(ILOAD, 1));

        il.add(new InsnNode(IADD));
        il.add(new VarInsnNode(ISTORE, 2));

        il.add(new VarInsnNode(ALOAD, 0));
        il.add(new VarInsnNode(ILOAD, 2));

        il.add(new MethodInsnNode(INVOKEVIRTUAL, BEAN, "setV", "(I)V", false));

        il.add(new VarInsnNode(ILOAD, 2));
        il.add(new InsnNode(IRETURN));

        mn.maxLocals = 3;
        mn.maxStack = 3;
        return mn;
    }

    private MethodNode createSubMethodNode() {
        MethodNode mn = new MethodNode(ACC_PUBLIC, "sub","(I)I", null, null);
        InsnList il = mn.instructions;
        il.add(new VarInsnNode(ALOAD, 0));
        il.add(new MethodInsnNode(INVOKEVIRTUAL, BEAN, "getV", "()I", false));
        il.add(new VarInsnNode(ILOAD, 1));

        il.add(new InsnNode(ISUB));
        il.add(new VarInsnNode(ISTORE, 2));

        il.add(new VarInsnNode(ALOAD, 0));
        il.add(new VarInsnNode(ILOAD, 2));

        il.add(new MethodInsnNode(INVOKEVIRTUAL, BEAN, "setV", "(I)V", false));

        il.add(new VarInsnNode(ILOAD, 2));
        il.add(new InsnNode(IRETURN));

        mn.maxLocals = 3;
        mn.maxStack = 3;
        return mn;
    }
}
