package com.xiaoqq.practise.threadmonitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;

/**
 * Created by rxiao on 3/31/15.
 */
public class MonitorThreadClassVisitor extends ClassVisitor{
    private String className;
    private int api;
    public MonitorThreadClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
        this.api = api;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println("---------MonitorThreadClassVisitor class:"+name+" visit.");
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        System.out.println("---------MonitorThreadClassVisitor method:"+name+desc+" visit.");
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        //if (isThreadRunningMethod(className, name, desc)) {
            return new MonitorThreadAdviceAdapter(api, mv, access, name, desc, className);
        //}
        //return mv;
    }

    private static boolean isThreadRunningMethod(String clazzName, String methodName, String desc) {
        if (MonitorUtil.isAssignableFrom(MonitorUtil.THREAD_TYPE_BYTECODE_NAME, clazzName)
                && methodName.equals("start")
                && desc.equals("()V")) { //Thread.start()
            return true;
        }
        if (MonitorUtil.isAssignableFrom(MonitorUtil.RUNNABLE_TYPE_BYTECODE_NAME, clazzName)
                && methodName.equals("run")
                && desc.equals("()V")) { //Runnable.run()
            return true;
        }
        if (MonitorUtil.isAssignableFrom(MonitorUtil.CALLABLE_TYPE_BYTECODE_NAME, clazzName)
                && methodName.equals("call")
                && desc.equals("()V")) { //Runnable.run()
            return true;
        }
        return false;
    }
}
