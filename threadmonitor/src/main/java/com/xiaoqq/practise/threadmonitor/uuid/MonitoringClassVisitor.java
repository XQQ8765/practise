package com.xiaoqq.practise.threadmonitor.uuid;

import com.xiaoqq.practise.threadmonitor.addproperty.ThreadConstructAdviceAdapter;
import com.xiaoqq.practise.threadmonitor.addproperty.ThreadRunningAdviceAdapter;
import com.xiaoqq.practise.threadmonitor.util.MonitorUtil;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static com.xiaoqq.practise.threadmonitor.util.IConstant.*;
import static org.objectweb.asm.Opcodes.*;

/**
 * Created by rxiao on 3/31/15.
 */
public class MonitoringClassVisitor extends ClassVisitor {
    private String className;
    private String superName;
    private String[] interfaces;
    private int api;
    private ClassLoader classLoader;

    public MonitoringClassVisitor(int api, ClassVisitor cv, ClassLoader classLoader) {
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
        if (isMonitoringMethod(name, desc)) {
            System.out.println("Instrumentation method: " + className + "." + name + desc);
            return new MonitoringMethodAdviceAdapter(api, mv, access, name, desc, className);
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        if (isServletClass(superName)) {
            //Add method "saveHash()"
            addMethodSaveHash();

            //Add method "removeHash()"
            addMethodRemoveHash();
        }
        super.visitEnd();
    }

    private boolean isServletClass(String superName) {
        return (MonitorUtil.isAssignableFrom(MonitorUtil.HTTP_SERVLET_BYTECODE_NAME, superName, classLoader));
    }

    private boolean isMonitoringMethod(String methodName, String desc) {
        //TODO determine whether it's a servlet class
        if (("doGet".equals(methodName) && "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)V".equals(desc)
             || ("doPost".equals(methodName) && "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)V".equals(desc)))) {
            return true;
        }
        return false;
    }

    /**
     * Source code of this method:
     private void saveHash() {
         String UUID = HashUtil.generateUUID();
         String objectHashId = HashUtil.getCurrentThreadHash();
         HashStorage.put(objectHashId, UUID);
     }
     */
    private void addMethodSaveHash() {
        {
            MethodVisitor mv = super.visitMethod(ACC_PRIVATE, METHOD_SAVE_HASH, "()V", null, null);
            mv.visitCode();
            mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashUtil", "generateUUID", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashUtil", "getCurrentThreadHash", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 2);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashStorage", "put", "(Ljava/lang/String;Ljava/lang/String;)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 3);
            mv.visitEnd();
        }
    }

    /**
     * Source code of this method:
     private void removeHash() {
         String objectHashId = HashUtil.getCurrentThreadHash();
         HashStorage.remove(objectHashId);
     }
     */
    private void addMethodRemoveHash() {
        {
            MethodVisitor mv = super.visitMethod(ACC_PRIVATE, METHOD_REMOVE_HASH, "()V", null, null);
            mv.visitCode();
            mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashUtil", "getCurrentThreadHash", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "com/xiaoqq/practise/threadmonitor/uuid/HashStorage", "remove", "(Ljava/lang/String;)Ljava/lang/String;", false);
            mv.visitInsn(POP);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 2);
            mv.visitEnd();
        }
    }
}
