package com.xiaoqq.practise.threadmonitor.uuid;

import com.xiaoqq.practise.threadmonitor.MonitorUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.Label;
import static org.objectweb.asm.Opcodes.*;
import static com.xiaoqq.practise.threadmonitor.uuid.IConstant.*;

/**
 * Created by rxiao on 3/31/15.
 */
public class UUIDThreadAdviceAdapter extends AdviceAdapter {
    private String methodName;
    private String className;

    /**
     * Creates a new {@link org.objectweb.asm.commons.AdviceAdapter}.
     *
     * @param api    the ASM API version implemented by this visitor. Must be one
     *               of {@link org.objectweb.asm.Opcodes#ASM4} or {@link org.objectweb.asm.Opcodes#ASM5}.
     * @param mv     the method visitor to which this adapter delegates calls.
     * @param access the method's access flags (see {@link org.objectweb.asm.Opcodes}).
     * @param name   the method's name.
     * @param desc   the method's descriptor (see {@link java.lang.reflect.Type Type}).
     */
    protected UUIDThreadAdviceAdapter(int api, MethodVisitor mv, int access, String name, String desc, String className) {
        super(api, mv, access, name, desc);
        this.className = className;
        this.methodName = name;
    }

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

        Label l0 = new Label();
        mv.visitLabel(l0);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, className, "printThreadRelationship", "()V", false);
        /*
        addMethodPrintCurrentThreadName();
        addMethodPrintParentThreadName();
        */
        super.onMethodEnter();
    }
    /*
    protected void addMethodPrintCurrentThreadName() {
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
    }
    */

    protected void addMethodPrintParentThreadName() {
        {
            //Generate Code: String threadName = xxx_parent_threadBean.getThreadName();
            mv.visitFieldInsn(GETFIELD, className, "xxx_parent_threadBean", "Lcom/xiaoqq/practise/threadmonitor/uuid/ThreadBean;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/xiaoqq/practise/threadmonitor/uuid/ThreadBean", "getThreadName", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitMaxs(1, 2);

            /*
            //Generate Code: System.out.println("$$$Parent Thread Name:" + xxx_parent_threadBean.getThreadName());
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitLdcInsn("$$$Parent Thread Name:");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);

            /*mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, className, PROPERTY_THREAD_BEAN_NAME, "Lcom/xiaoqq/practise/threadmonitor/uuid/ThreadBean;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/xiaoqq/practise/threadmonitor/uuid/ThreadBean", "getThreadName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false); */

            /*
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            */
        }
    }

    @Override
    protected void onMethodExit(int opcode) {
        System.out.println("###onMethodExit():  className:" + className + ", methodName:" + methodName +", Thread:" + MonitorUtil.getCurrentThreadName() + " finish.");
        super.onMethodExit(opcode);
    }
}
