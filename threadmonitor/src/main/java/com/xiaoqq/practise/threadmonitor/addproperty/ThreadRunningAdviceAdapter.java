package com.xiaoqq.practise.threadmonitor.addproperty;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by rxiao on 3/31/15.
 */
public class ThreadRunningAdviceAdapter extends AdviceAdapter {
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
    protected ThreadRunningAdviceAdapter(int api, MethodVisitor mv, int access, String name, String desc, String className) {
        super(api, mv, access, name, desc);
        this.className = className;
        this.methodName = name;
    }

    @Override
    protected void onMethodEnter() {
        //System.out.println("###onMethodEnter(): className:" + className + ", methodName:" + methodName + ", Thread:" + MonitorUtil.getCurrentThreadName() + " is running.");

        //Label l0 = new Label();
        //mv.visitLabel(l0);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, className, "printThreadRelationship", "()V", false);

        super.onMethodEnter();
    }
}
