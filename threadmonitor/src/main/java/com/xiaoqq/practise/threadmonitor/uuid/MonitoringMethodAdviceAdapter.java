package com.xiaoqq.practise.threadmonitor.uuid;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;
import static com.xiaoqq.practise.threadmonitor.util.IConstant.*;

/**
 * Created by rxiao on 3/31/15.
 */
public class MonitoringMethodAdviceAdapter extends AdviceAdapter {
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
    protected MonitoringMethodAdviceAdapter(int api, MethodVisitor mv, int access, String name, String desc, String className) {
        super(api, mv, access, name, desc);
        this.className = className;
        this.methodName = name;
    }

    @Override
    protected void onMethodEnter() {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, className, METHOD_SAVE_HASH, "()V", false);

        super.onMethodEnter();
    }

    @Override
    protected void onMethodExit(int opcode) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, className, METHOD_REMOVE_HASH, "()V", false);

        super.onMethodExit(opcode);
    }
}
