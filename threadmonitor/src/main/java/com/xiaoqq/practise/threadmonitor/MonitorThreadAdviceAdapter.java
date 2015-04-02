package com.xiaoqq.practise.threadmonitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.lang.reflect.Type;

/**
 * Created by rxiao on 3/31/15.
 */
public class MonitorThreadAdviceAdapter extends AdviceAdapter {
    private String methodName;
    private String className;

    /**
     * Creates a new {@link org.objectweb.asm.commons.AdviceAdapter}.
     *
     * @param api    the ASM API version implemented by this visitor. Must be one
     *               of {@link Opcodes#ASM4} or {@link Opcodes#ASM5}.
     * @param mv     the method visitor to which this adapter delegates calls.
     * @param access the method's access flags (see {@link Opcodes}).
     * @param name   the method's name.
     * @param desc   the method's descriptor (see {@link Type Type}).
     */
    protected MonitorThreadAdviceAdapter(int api, MethodVisitor mv, int access, String name, String desc, String className) {
        super(api, mv, access, name, desc);
        this.className = className;
        this.methodName = name;
    }
    /*
    @Override
    public void visitInsn(int opcode) {
        System.out.println("###visitInsn(): className:" + className + ", methodName:" + methodName
                +  ", opcode:" + opcode);
        super.visitInsn(opcode);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        System.out.println("###visitMethodInsn(): className:" + className + ", methodName:" + methodName +  "opcode:" + opcode + ", invoke method:" + owner + "."+ name + desc);
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
    */

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (opcode == NEW && MonitorUtil.isThreadTypeOrInterfaces(type)) {
            System.out.println("###visitTypeInsn(): className:" + className + ", methodName:" + methodName +", Thread:" + MonitorUtil.getCurrentThreadName() + ". New a Thread.");
        }
        super.visitTypeInsn(opcode, type);
    }

    @Override
    protected void onMethodEnter() {
        System.out.println("###onMethodEnter(): className:" + className + ", methodName:" + methodName + ", Thread:" + MonitorUtil.getCurrentThreadName() + " is running.");

        //Invoke method "String MonitorUtil.getCurrentThreadName()"
        this.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/MonitorUtil", "getCurrentThreadName", "()Ljava/lang/String;", false);
        mv.visitVarInsn(ASTORE, 1);

        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitLdcInsn("$$$Current Thread Name:");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);


        super.onMethodEnter();
    }

    @Override
    protected void onMethodExit(int opcode) {
        System.out.println("###onMethodExit():  className:" + className + ", methodName:" + methodName +", Thread:" + MonitorUtil.getCurrentThreadName() + " finish.");
        super.onMethodExit(opcode);
    }
}
