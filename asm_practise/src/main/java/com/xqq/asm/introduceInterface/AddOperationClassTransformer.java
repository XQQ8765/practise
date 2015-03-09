package com.xqq.asm.introduceInterface;

import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by xiaoqq on 15-3-9.
 */

public class AddOperationClassTransformer{
    public static final String BEAN = "com/xqq/asm/introduceInterface/C";

    public void transform(ClassNode cn) {
        cn.superName = cn.name;
        cn.name = cn.name + "_Sub";
        cn.access = ACC_PUBLIC + ACC_FINAL;

        Iterator<MethodNode> itr = cn.methods.iterator();
        while (itr.hasNext()) {
            MethodNode methodNode = itr.next();
            if (methodNode.name.equals("<init>")) {
                itr.remove();
            }
        }

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
