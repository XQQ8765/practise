package com.xiaoqq.practise.threadmonitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Created by rxiao on 3/31/15.
 */
public class MonitorThreadClassVisitor extends ClassVisitor{
    private String className;
    private String superName;
    private String[] interfaces;
    private int api;
    public MonitorThreadClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
        this.api = api;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
        this.superName = superName;
        this.interfaces = interfaces;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        System.out.println("---------MonitorThreadClassVisitor className:" + className + ", method:"+name+desc+" visit.");
        if (isThreadRunningMethod(superName, interfaces, name, desc)) {
            return new MonitorThreadAdviceAdapter(api, mv, access, name, desc, className);
            //return new SimpleMethodVisitor(api, mv);
            //return new SimpleAdviceAdapter(api, mv, access, name, desc);
        }
        return mv;
    }

    private boolean isThreadRunningMethod(String superName, String[] interfaces, String methodName, String desc) {
        if (MonitorUtil.isAssignableFrom(MonitorUtil.THREAD_TYPE_BYTECODE_NAME, superName)
                && methodName.equals("run")
                && desc.equals("()V")) { //Thread.start()
            return true;
        }
        if (interfaces != null) {
            for (String interface_name : interfaces) {
                if (MonitorUtil.isAssignableFrom(MonitorUtil.RUNNABLE_TYPE_BYTECODE_NAME, interface_name)) {
                    if (methodName.equals("run") && desc.equals("()V")) { //Runnable.run()
                        return true;
                    }
                }
                if (MonitorUtil.isAssignableFrom(MonitorUtil.CALLABLE_TYPE_BYTECODE_NAME, interface_name)) {
                    if (methodName.equals("call") && desc.contains("()")) { //V call()
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
