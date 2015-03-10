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
    private ClassNode classNode;

    public AddOperationClassTransformer(ClassNode cn) {
        super(ASM4, cn);
        this.classNode = cn;
    }

    @Override
    public void visitEnd() {
        transform();
        super.visitEnd();
    }

    public void transform() {
        MethodNode mn = createConstructMethodNode();
        classNode.methods.add(mn);

        classNode.interfaces.add("com/xqq/asm/introduceInterface/IAdd");
        mn = createAddMethodNode();
        classNode.methods.add(mn);

        classNode.interfaces.add("com/xqq/asm/introduceInterface/ISub");
        mn = createSubMethodNode();
        classNode.methods.add(mn);

    }

    private MethodNode createConstructMethodNode() {
        MethodNode mn = new MethodNode(ACC_PUBLIC, "<init>","(I)V", null, null);
        InsnList il = mn.instructions;
        il.add(new VarInsnNode(ALOAD, 0));
        il.add(new VarInsnNode(ILOAD, 1));

        //invoke super construct
        il.add(new MethodInsnNode(INVOKESPECIAL, classNode.superName, "<init>", "(I)V", false));
        il.add(new InsnNode(RETURN));

        mn.maxLocals = 2;
        mn.maxStack = 2;
        return mn;
    }

    private MethodNode createAddMethodNode() {
        MethodNode mn = new MethodNode(ACC_PUBLIC, "add","(I)I", null, null);
        InsnList il = mn.instructions;
        il.add(new VarInsnNode(ALOAD, 0));
        //invoke this.getK()
        il.add(new MethodInsnNode(INVOKEVIRTUAL, classNode.name, "getK", "()I", false));
        il.add(new VarInsnNode(ILOAD, 1));

        il.add(new InsnNode(IADD));
        il.add(new VarInsnNode(ISTORE, 2));

        il.add(new VarInsnNode(ALOAD, 0));
        il.add(new VarInsnNode(ILOAD, 2));

        //invoke this.setK(I)
        il.add(new MethodInsnNode(INVOKEVIRTUAL, classNode.name, "setK", "(I)V", false));

        il.add(new VarInsnNode(ILOAD, 2));
        il.add(new InsnNode(IRETURN));

        mn.maxLocals = 3;
        mn.maxStack = 2;
        return mn;
    }

    private MethodNode createSubMethodNode() {
        MethodNode mn = new MethodNode(ACC_PUBLIC, "sub","(I)I", null, null);
        InsnList il = mn.instructions;
        il.add(new VarInsnNode(ALOAD, 0));
        //invoke this.getK()
        il.add(new MethodInsnNode(INVOKEVIRTUAL, classNode.name, "getK", "()I", false));
        il.add(new VarInsnNode(ILOAD, 1));

        il.add(new InsnNode(ISUB));
        il.add(new VarInsnNode(ISTORE, 2));

        il.add(new VarInsnNode(ALOAD, 0));
        il.add(new VarInsnNode(ILOAD, 2));

        //invoke this.setK(I)
        il.add(new MethodInsnNode(INVOKEVIRTUAL, classNode.name, "setK", "(I)V", false));

        il.add(new VarInsnNode(ILOAD, 2));
        il.add(new InsnNode(IRETURN));

        mn.maxLocals = 3;
        mn.maxStack = 2;
        return mn;
    }
}
