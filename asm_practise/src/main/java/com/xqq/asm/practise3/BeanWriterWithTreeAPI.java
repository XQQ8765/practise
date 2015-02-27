package com.xqq.asm.practise3;

import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

/**
 Use the tree API to generate class:
 <code>
 package org.xqq.test;

 public class Bean {
 private int f;
 public int getF() {
 return f;
 }
 public void setF(int f) {
 this.f = f;
 }
 public void checkAndSetF(int f) {
 if (f >= 1 && f <= 10) {
 this.f = f;
 } else {
 String errorMsg = "Only 1<=f<=10 is allowed.";
 System.err.println(errorMsg);
 throw new IllegalArgumentException(errorMsg);
 }
 }
 }
 </code>
 */
public class BeanWriterWithTreeAPI {
    public static final String BEAN = "org/xqq/test/Bean";
    public static final String BEAN_DOT_NAME = "org.xqq.test.Bean";
    public static final String FIELD_F = "f";

    public byte[] writeClass() {
        ClassNode cn = new ClassNode();
        cn.version = V1_7;
        cn.access = ACC_PUBLIC;
        cn.name = BEAN;
        cn.superName = "java/lang/Object";

        //Create integer field "f"
        cn.fields.add(new FieldNode(ACC_PRIVATE, "f", "I", null, null));

        //Create default constructor, the constructor is required if we would like to new an instance of this class via reflection.
        MethodNode constructMethodNode = createConstructMethodNode();
        cn.methods.add(constructMethodNode);

        //Create method "getF"
        MethodNode getFMethodNode = createGetFMethodNode();
        cn.methods.add(getFMethodNode);

        //Create method "setF"
        MethodNode setFMethodNode = createSetFMethodNode();
        cn.methods.add(setFMethodNode);

        //Create method "checkAndSetF"
        MethodNode checkAndSetFMethodNode = createCheckAndSetFMethodNode();
        cn.methods.add(checkAndSetFMethodNode);

        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }



    private MethodNode createConstructMethodNode() {
        MethodNode mn = new MethodNode(ACC_PUBLIC, "<init>", "()V", null, null);
        InsnList il = mn.instructions;
        il.add(new VarInsnNode(ALOAD, 0));
        il.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false));
        il.add(new InsnNode(RETURN));

        mn.maxLocals = 1;
        mn.maxStack = 1;
        return mn;
    }

    private MethodNode createGetFMethodNode() {
        MethodNode mn = new MethodNode(ACC_PUBLIC, "getF","()I", null, null);
        InsnList il = mn.instructions;
        il.add(new VarInsnNode(ALOAD, 0));
        il.add(new FieldInsnNode(GETFIELD, BEAN, FIELD_F, "I"));
        il.add(new InsnNode(IRETURN));

        mn.maxLocals = 1;
        mn.maxStack = 1;
        return mn;
    }

    private MethodNode createSetFMethodNode() {
        MethodNode mn = new MethodNode(ACC_PUBLIC, "setF","(I)V", null, null);
        InsnList il = mn.instructions;
        il.add(new VarInsnNode(ALOAD, 0));
        il.add(new VarInsnNode(ILOAD, 1));
        il.add(new FieldInsnNode(PUTFIELD, BEAN, FIELD_F, "I"));
        il.add(new InsnNode(RETURN));

        mn.maxLocals = 2;
        mn.maxStack = 2;
        return mn;
    }

    /**
     * The source code for this method is:
     * <code>
     public void checkAndSetF(int f) {
     if (f >= 1 && f <= 10) {
     this.f = f;
     } else {
     String errorMsg = "Only 1<=f<=10 is allowed.";
     System.err.println(errorMsg);
     throw new IllegalArgumentException(errorMsg);
     }
     }
     * </code>
     */
    private MethodNode createCheckAndSetFMethodNode() {
        MethodNode mn = new MethodNode(ACC_PUBLIC, "checkAndSetF","(I)V", null, null);
        InsnList il = mn.instructions;
        LabelNode exceptionLable = new LabelNode();

        //f<1, Goto exceptionLabel
        il.add(new VarInsnNode(ILOAD, 1));
        il.add(new InsnNode(ICONST_1));
        il.add(new JumpInsnNode(IF_ICMPLT, exceptionLable));

        //f>10, Goto exceptionLabel
        il.add(new VarInsnNode(ILOAD, 1));
        il.add(new VarInsnNode(BIPUSH, 10));
        il.add(new JumpInsnNode(IF_ICMPGT, exceptionLable));

        //1<=f<=10, this.f=f;
        il.add(new VarInsnNode(ALOAD, 0));//Load this to operand stack
        il.add(new VarInsnNode(ILOAD, 1));
        il.add(new FieldInsnNode(PUTFIELD, BEAN, FIELD_F, "I"));
        il.add(new InsnNode(RETURN));

        //exceptionLabel
        il.add(exceptionLable);
        il.add(new FrameNode(F_SAME, 0, null, 0, null));

        //Generate code: System.err.println("Only 1<=f<=10 is allowed.")
        il.add(new LdcInsnNode("Only 1<=f<=10 is allowed."));
        il.add(new VarInsnNode(ASTORE, 2));//Store the above errorMsg string into localvariable[2].
        il.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;"));
        il.add(new VarInsnNode(ALOAD, 2));
        il.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));

        //throw new IllegalArgumentException(errorMessage)
        il.add(new TypeInsnNode(NEW, "java/lang/IllegalArgumentException"));
        il.add(new InsnNode(DUP));
        il.add(new VarInsnNode(ALOAD, 2));
        il.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false));
        il.add(new InsnNode(ATHROW));

        mn.maxLocals = 3;
        mn.maxStack = 3;
        return mn;
    }

    private void wirteCheckAndSetFMethod(MethodVisitor mv) {
        Label exceptionLable = new Label();
        mv.visitCode();

        ////f<1, Goto exceptionLabel
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(ICONST_1);
        mv.visitJumpInsn(IF_ICMPLT, exceptionLable);

        //f>10, Goto exceptionLabel
        mv.visitVarInsn(ILOAD, 1);
        mv.visitVarInsn(BIPUSH, 10);
        mv.visitJumpInsn(IF_ICMPGT, exceptionLable);

        //1<=f<=10, this.f=f;
        mv.visitVarInsn(ALOAD, 0);//Load this to operand stack
        mv.visitVarInsn(ILOAD, 1);
        mv.visitFieldInsn(PUTFIELD, BEAN, FIELD_F, "I");
        mv.visitInsn(RETURN);// RETURN

        //exceptionLabel
        mv.visitLabel(exceptionLable);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        //Generate code: System.err.println("Only 1<=f<=10 is allowed.")
        mv.visitLdcInsn("Only 1<=f<=10 is allowed.");
        mv.visitVarInsn(ASTORE, 2);//Store the above errorMsg string into localvariable[2].
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        //throw new IllegalArgumentException(errorMessage)
        mv.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 2);//Load the errorMessage into operand stack, then pass it to the constructor for "IllegalArgumentException".
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
        mv.visitInsn(ATHROW);

        mv.visitMaxs(3, 3);
        mv.visitEnd();
    }
}
