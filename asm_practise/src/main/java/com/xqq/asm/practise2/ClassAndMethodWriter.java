package com.xqq.asm.practise2;

import org.objectweb.asm.*;
import org.objectweb.asm.util.TraceClassVisitor;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

/**
 * Created by rxiao on 2/9/15.
 */
public class ClassAndMethodWriter {
    public static final String BEAN = "org/xqq/test/Bean";
    public static final String BEAN_DOT_NAME = "org.xqq.test.Bean";
    public static final String FIELD_F = "f";

    public byte[] writeClass() {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(V1_7, ACC_PUBLIC,
                BEAN, null, "java/lang/Object",
                null);
        //Create integer field "f"
        cw.visitField(ACC_PRIVATE, "f", "I",
                null, null).visitEnd();

        //Create default constructor, the constructor is required if we would like to new an instance of this class via reflection.
        MethodVisitor constructorMethodVisitor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        wirteConstructor(constructorMethodVisitor);

        //Create method "getF"
        MethodVisitor getFMethodVisitor = cw.visitMethod(ACC_PUBLIC, "getF","()I", null, null);
        wirteGetFMethod(getFMethodVisitor);

        //Create method "setF"
        MethodVisitor setFMethodVisitor = cw.visitMethod(ACC_PUBLIC, "setF","(I)V", null, null);
        wirteSetFMethod(setFMethodVisitor);

        //Create method "checkAndSetF"
        MethodVisitor checkAndSetFVisitor = cw.visitMethod(ACC_PUBLIC, "checkAndSetF","(I)V", null, null);
        wirteCheckAndSetFMethod(checkAndSetFVisitor);
        cw.visitEnd();
        return cw.toByteArray();
    }

    private void wirteConstructor(MethodVisitor mv) {
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private void wirteGetFMethod(MethodVisitor mv) {
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);//Why it's ALOAD rather than "ILOAD"???
        mv.visitFieldInsn(GETFIELD, BEAN, FIELD_F, "I");
        mv.visitInsn(IRETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private void wirteSetFMethod(MethodVisitor mv) {
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitFieldInsn(PUTFIELD, BEAN, FIELD_F, "I");
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
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
     * @param mv
     */
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
