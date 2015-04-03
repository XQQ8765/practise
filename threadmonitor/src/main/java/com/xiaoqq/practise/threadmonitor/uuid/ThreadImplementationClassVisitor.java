package com.xiaoqq.practise.threadmonitor.uuid;

import com.xiaoqq.practise.threadmonitor.util.MonitorUtil;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;
import static com.xiaoqq.practise.threadmonitor.util.IConstant.*;

/**
 * Created by rxiao on 3/31/15.
 */
public class ThreadImplementationClassVisitor extends ClassVisitor {
    private String className;
    private String superName;
    private String[] interfaces;
    private int api;

    public ThreadImplementationClassVisitor(int api, ClassVisitor cv) {
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
        //System.out.println("---------ThreadImplementationClassVisitor className:" + className + ", method:" + name + desc + " visit.");
        if (isThreadRunningMethod(superName, interfaces, name, desc)) {
            return new ThreadRunningAdviceAdapter(api, mv, access, name, desc, className);
        } else if (isThreadConstructMethod(superName, interfaces, name, desc)) {
            return new ThreadConstructAdviceAdapter(api, mv, access, name, desc, className);
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        if (isThreadClassOrInterface(superName, interfaces)) {
            //System.out.println("---------ThreadImplementationClassVisitor create Field:" + PROPERTY_THREAD_BEAN_NAME);
            //Add field "private ThreadBean xxx_parent_threadBean"
            super.visitField(
                    ACC_PRIVATE,
                    PROPERTY_THREAD_BEAN_NAME,
                    "Lcom/xiaoqq/practise/threadmonitor/model/ThreadBean;",
                    null,
                    null);

            //Add method "recordParentThreadMethod()"
            addMethodRecordParentThread();

            //Add method "printThreadRelationship()"
            addMethodPrintThreadRelationship();
        }
        super.visitEnd();
    }

    /**
     * Source code of this method:
     * private void printThreadRelationship() {
     * String parentThreadName =  xxx_parent_threadBean.getThreadName();
     * String currentThreadName = Thread.currentThread().getName();
     * StringBuilder sb = new StringBuilder();
     * sb.append("#####: Thread relationship: ");
     * sb.append(parentThreadName);
     * sb.append(" ---> ");
     * sb.append(currentThreadName);
     * System.out.println(sb.toString());
     * }
     */
    private void addMethodPrintThreadRelationship() {
        {
            //System.out.println("---------ThreadImplementationClassVisitor create Method:" + METHOD_PRINT_THRAD_RELATIONSHIP_NAME + "()V");
            MethodVisitor mv = super.visitMethod(ACC_PRIVATE, METHOD_PRINT_THRAD_RELATIONSHIP_NAME, "()V", null, null);
            mv.visitCode();

            //Genarate Code: System.out.println("#####xxx_parent_threadBean="+xxx_parent_threadBean);
            /*
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitLdcInsn("#####xxx_parent_threadBean=");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, className, "xxx_parent_threadBean", "Lcom/xiaoqq/practise/threadmonitor/model/ThreadBean;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            */

            //Genarate Code: String parentThreadName =  xxx_parent_threadBean.getThreadName();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, className, PROPERTY_THREAD_BEAN_NAME, "Lcom/xiaoqq/practise/threadmonitor/model/ThreadBean;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/xiaoqq/practise/threadmonitor/model/ThreadBean", "getThreadName", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 1);

            //Generate Code: String currentThreadName = Thread.currentThread().getName();
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getName", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 2);

            //Generate Code: StringBuilder sb = new StringBuilder();
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 3);

            //Generate Code:  sb.append("#####: Thread relationship: ");
            mv.visitVarInsn(ALOAD, 3);
            mv.visitLdcInsn("#####: Thread relationship: ");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);

            //Generate Code:  sb.append(parentThreadName);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);

            //Generate Code:  sb.append(" ---> ");
            mv.visitVarInsn(ALOAD, 3);
            mv.visitLdcInsn(" ---> ");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);

            //Generate Code:  sb.append(currentThreadName);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);

            //Generate Code:  System.out.println(sb.toString());
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            //Generate Code:  return
            mv.visitInsn(RETURN);

            mv.visitMaxs(2, 4);
            mv.visitEnd();
        }
    }

    /**
     * Source code of this method:
     * private void recordParentThread() {
     * long threadId = Thread.currentThread().getId();
     * String threadName = Thread.currentThread().getName();
     * xxx_parent_threadBean = new ThreadBean(threadId, threadName);
     * }
     */
    private void addMethodRecordParentThread() {
        //System.out.println("---------ThreadImplementationClassVisitor create Method:" + METHOD_RECORD_PARENT_THREAD_NAME + "()V");
        MethodVisitor mv = super.visitMethod(ACC_PRIVATE, METHOD_RECORD_PARENT_THREAD_NAME, "()V", null, null);
        mv.visitCode();

        //Generate Code: long threadId = Thread.currentThread().getId();
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J", false);
        mv.visitVarInsn(LSTORE, 1);

        //Generate Code: String threadName = Thread.currentThread().getName();
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getName", "()Ljava/lang/String;", false);
        mv.visitVarInsn(ASTORE, 3);

        //Generate Code:  xxx_parent_threadBean = new ThreadBean(threadId, threadName);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(NEW, "com/xiaoqq/practise/threadmonitor/model/ThreadBean");
        mv.visitInsn(DUP);
        mv.visitVarInsn(LLOAD, 1);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKESPECIAL, "com/xiaoqq/practise/threadmonitor/model/ThreadBean", "<init>", "(JLjava/lang/String;)V", false);
        mv.visitFieldInsn(PUTFIELD, className, "xxx_parent_threadBean", "Lcom/xiaoqq/practise/threadmonitor/model/ThreadBean;");

        mv.visitInsn(RETURN);

        mv.visitMaxs(6, 4);
        mv.visitEnd();
    }

    private boolean isThreadClassOrInterface(String superName, String[] interfaces) {
        if (MonitorUtil.isAssignableFrom(MonitorUtil.THREAD_TYPE_BYTECODE_NAME, superName)) {
            return true;
        }
        if (interfaces != null) {
            for (String interface_name : interfaces) {
                if (MonitorUtil.isAssignableFrom(MonitorUtil.RUNNABLE_TYPE_BYTECODE_NAME, interface_name)
                        || MonitorUtil.isAssignableFrom(MonitorUtil.CALLABLE_TYPE_BYTECODE_NAME, interface_name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isThreadRunningMethod(String superName, String[] interfaces, String methodName, String desc) {
        if (MonitorUtil.isAssignableFrom(MonitorUtil.THREAD_TYPE_BYTECODE_NAME, superName)
                && methodName.equals("run")
                && desc.equals("()V")) { //Thread.run()
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

    private boolean isThreadConstructMethod(String superName, String[] interfaces, String methodName, String desc) {
        if (!methodName.equals("<init>")) {
            return false;
        }

        return isThreadClassOrInterface(superName, interfaces);
    }
}
