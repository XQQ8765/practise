package com.dell.sample.instrumentation.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import static com.dell.sample.instrumentation.asm.IConstant.*;
/**
 * Created by rxiao on 4/15/15.
 */
public class PollWebSiteServletClassVisitor extends ClassVisitor {
    private boolean isRetransformAgent;
    private int agentId;
    private String className;
    private ClassLoader classLoader;

    public PollWebSiteServletClassVisitor(ClassVisitor cv, ClassLoader classLoader) {
        super(IConstant.ASM_API, cv);
        this.classLoader = classLoader;
        this.isRetransformAgent = false;
    }

    public PollWebSiteServletClassVisitor(ClassVisitor cv, ClassLoader classLoader, int agentId) {
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
        if (CLASS_NAME_POLL_WEB_SITE_SERVLET.equals(ClassUtil.bytecodeClassNameToJavaClassName(className))
                && name.equals("doPost")
                && desc.equals("(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)V")) {
            if (isRetransformAgent) {
                return new PollWebSiteServletAdviceAdapter(mv, access, name, desc, agentId);
            } else {
                return new PollWebSiteServletAdviceAdapter(mv, access, name, desc);
            }
        }
        return mv;
    }
}
