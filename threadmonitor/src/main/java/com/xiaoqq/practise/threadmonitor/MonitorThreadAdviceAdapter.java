package com.xiaoqq.practise.threadmonitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.Opcodes;

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

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (opcode == NEW && MonitorUtil.isThreadTypeOrInterfaces(type)) {
            System.out.println("New a thread: " + type);
        }
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if (opcode == INVOKESPECIAL && "<init>".equals(name) && MonitorUtil.isThreadTypeOrInterfaces(owner)) {// Thread Construct
        }
        if (opcode == INVOKESPECIAL) {
            if (MonitorUtil.isAssignableFrom(MonitorUtil.THREAD_TYPE_BYTECODE_NAME, owner) && name.equals("start")) {//Thread start method
                System.out.println("visitMethodInsn(): Thread:" + MonitorUtil.getCurrentThreadName() + "start()");
            }
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    protected void onMethodEnter() {
        System.out.println("onMethodEnter(): className:" + className + ", methodName:" + methodName + ", Thread:" + MonitorUtil.getCurrentThreadName() + " start().");
        super.onMethodEnter();
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        System.out.println("onMethodEnter():  className:" + className + ", methodName:" + methodName +", Thread:" + MonitorUtil.getCurrentThreadName() + " finish().");
    }
}
