package com.xiaoqq.practise.threadmonitor.uuid;

import com.xiaoqq.practise.threadmonitor.util.MonitorUtil;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static com.xiaoqq.practise.threadmonitor.util.IConstant.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Created by rxiao on 3/31/15.
 */
public class ThreadImplementationClassVisitor extends ClassVisitor {
    private String className;
    private String superName;
    private String[] interfaces;
    private int api;
    private ClassLoader classLoader;

    public ThreadImplementationClassVisitor(int api, ClassVisitor cv, ClassLoader classLoader) {
        super(api, cv);
        this.api = api;
        this.classLoader = classLoader;
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

        if (isThreadRunningMethod(superName, interfaces, name, desc)) {
            System.out.println("ThreadImplementationClassVisitor: Instrumentation method: " + className + "." + name + desc);
            return new ThreadRunningAdviceAdapter(api, mv, access, name, desc, className);
        } else if (isThreadConstructMethod(superName, interfaces, name, desc)) {
            System.out.println("ThreadImplementationClassVisitor: Instrumentation method: " + className + "." + name + desc);
            return new ThreadConstructAdviceAdapter(api, mv, access, name, desc, className);
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        if (isThreadClassOrInterface(superName, interfaces)) {
            addMethodPostConstructor();
            addMethodBeforeRun();
            addMethodEndRun();
        }
        super.visitEnd();
    }

    /**
     * Source code of this method:
     private void postConstructor() {
         String objectHashId = HashUtil.getCurrentThreadHash();
         String UUID = HashStorage.getByObjectHashId(objectHashId);
         String thisHashId = HashUtil.getObjectHash(this);
         HashStorage.put(thisHashId, UUID);
     }
     */
    private void addMethodPostConstructor() {
            MethodVisitor mv = super.visitMethod(ACC_PRIVATE, METHOD_POST_CONSTRUCTOR, "()V", null, null);
            mv.visitCode();
            mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashUtil", "getCurrentThreadHash", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 1);

            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashStorage", "getByObjectHashId", "(Ljava/lang/String;)Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 2);

            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashUtil", "getObjectHash", "(Ljava/lang/Object;)Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 3);

            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashStorage", "put", "(Ljava/lang/String;Ljava/lang/String;)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 4);
            mv.visitEnd();
    }

    /**
     * Source code of this method:
     private void beforeRun() {
         String thisHashId = HashUtil.getObjectHash(this);
         String UUID = HashStorage.getByObjectHashId(thisHashId);
         String currentThreadHash = HashUtil.getCurrentThreadHash();
         HashStorage.put(currentThreadHash, UUID);
     }
     */
    private void addMethodBeforeRun() {
        MethodVisitor mv = super.visitMethod(ACC_PRIVATE, METHOD_BEFORE_RUN, "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashUtil", "getObjectHash", "(Ljava/lang/Object;)Ljava/lang/String;", false);
        mv.visitVarInsn(ASTORE, 1);

        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashStorage", "getByObjectHashId", "(Ljava/lang/String;)Ljava/lang/String;", false);
        mv.visitVarInsn(ASTORE, 2);

        mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashUtil", "getCurrentThreadHash", "()Ljava/lang/String;", false);
        mv.visitVarInsn(ASTORE, 3);

        mv.visitVarInsn(ALOAD, 3);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashStorage", "put", "(Ljava/lang/String;Ljava/lang/String;)V", false);
        mv.visitInsn(RETURN);

        mv.visitMaxs(2, 4);
        mv.visitEnd();
    }

    /**
     * Source code of this method:
     private void endRun() {
         String currentThreadHash = HashUtil.getCurrentThreadHash();
         String thisHashId = HashUtil.getObjectHash(this);
         String UUID = HashStorage.getByObjectHashId(thisHashId);

         //Submit trace performance data by UUID
         System.out.println("Thread Hash:" + currentThreadHash + "Object Hash:" + thisHashId + ", UUID:" + UUID);

         HashStorage.remove(currentThreadHash);
         HashStorage.remove(thisHashId);
     }
     */
    private void addMethodEndRun() {
        MethodVisitor mv = super.visitMethod(ACC_PRIVATE, "endRun", "()V", null, null);
        mv.visitCode();
        //String currentThreadHash = HashUtil.getCurrentThreadHash();
        mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashUtil", "getCurrentThreadHash", "()Ljava/lang/String;", false);
        mv.visitVarInsn(ASTORE, 1);

        //String thisHashId = HashUtil.getObjectHash(this);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashUtil", "getObjectHash", "(Ljava/lang/Object;)Ljava/lang/String;", false);
        mv.visitVarInsn(ASTORE, 2);

        //String UUID = HashStorage.getByObjectHashId(thisHashId);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashStorage", "getByObjectHashId", "(Ljava/lang/String;)Ljava/lang/String;", false);
        mv.visitVarInsn(ASTORE, 3);

        //System.out.println("Thread Hash:" + currentThreadHash + "Object Hash:" + thisHashId + ", UUID:" + UUID);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitLdcInsn("Thread Hash:");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitLdcInsn("Object Hash:");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitLdcInsn(", UUID:");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        //HashStorage.remove(currentThreadHash);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashStorage", "remove", "(Ljava/lang/String;)Ljava/lang/String;", false);
        mv.visitInsn(POP);

        //HashStorage.remove(thisHashId);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashStorage", "remove", "(Ljava/lang/String;)Ljava/lang/String;", false);
        mv.visitInsn(POP);
        mv.visitInsn(RETURN);
        mv.visitMaxs(3, 4);
        mv.visitEnd();
    }

    private boolean isThreadClassOrInterface(String superName, String[] interfaces) {
        if (MonitorUtil.isAssignableFrom(MonitorUtil.THREAD_TYPE_BYTECODE_NAME, superName, classLoader)) {
            return true;
        }
        if (interfaces != null) {
            for (String interface_name : interfaces) {
                if (MonitorUtil.isAssignableFrom(MonitorUtil.RUNNABLE_TYPE_BYTECODE_NAME, interface_name, classLoader)
                        || MonitorUtil.isAssignableFrom(MonitorUtil.CALLABLE_TYPE_BYTECODE_NAME, interface_name, classLoader)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isThreadRunningMethod(String superName, String[] interfaces, String methodName, String desc) {
        if (MonitorUtil.isAssignableFrom(MonitorUtil.THREAD_TYPE_BYTECODE_NAME, superName, classLoader)
                && methodName.equals("run")
                && desc.equals("()V")) { //Thread.run()
            return true;
        }
        if (interfaces != null) {
            for (String interface_name : interfaces) {
                if (MonitorUtil.isAssignableFrom(MonitorUtil.RUNNABLE_TYPE_BYTECODE_NAME, interface_name, classLoader)) {
                    if (methodName.equals("run") && desc.equals("()V")) { //Runnable.run()
                        return true;
                    }
                }
                if (MonitorUtil.isAssignableFrom(MonitorUtil.CALLABLE_TYPE_BYTECODE_NAME, interface_name, classLoader)) {
                    if (methodName.equals("call") && desc.contains("()")) { //V call()
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isThreadConstructMethod(String superName, String[] interfaces, String methodName, String desc) {
        if (!methodName.equals("<init>")) {
            return false;
        }

        return isThreadClassOrInterface(superName, interfaces);
    }
}
