package com.dell.sample.instrumentation.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static com.dell.sample.instrumentation.asm.IConstant.CLASS_NAME_POLLER;
import static com.dell.sample.instrumentation.asm.IConstant.CLASS_NAME_POLLING_SERVICE;

/**
 * Created by rxiao on 4/15/15.
 */
public class PollingServiceClassVisitor extends ClassVisitor {
    private boolean isRetransformAgent;
    private int agentId;
    private String className;
    private ClassLoader classLoader;

    public PollingServiceClassVisitor(ClassVisitor cv, ClassLoader classLoader) {
        super(IConstant.ASM_API, cv);
        this.classLoader = classLoader;
        this.isRetransformAgent = false;
    }

    public PollingServiceClassVisitor(ClassVisitor cv, ClassLoader classLoader, int agentId) {
        super(IConstant.ASM_API, cv);
        this.classLoader = classLoader;
        this.isRetransformAgent = true;
        this.agentId = agentId;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if (CLASS_NAME_POLLING_SERVICE.equals(ClassUtil.bytecodeClassNameToJavaClassName(className))
                && name.equals("ping")) {
                //&& desc.equals("(Ljava/util/List;)Ljava/util/List;")) {
            if (isRetransformAgent) {
                return new PollingServiceAdviceAdapter(mv, access, name, desc, agentId);
            } else {
                return new PollingServiceAdviceAdapter(mv, access, name, desc);
            }
        }
        return mv;
    }
}
