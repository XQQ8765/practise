package com.xiaoqq.practise.threadmonitor.relationship;

import com.xiaoqq.practise.threadmonitor.relationship.model.CodePosition;
import com.xiaoqq.practise.threadmonitor.relationship.model.EventListener;
import com.xiaoqq.practise.threadmonitor.relationship.model.EventType;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * Created by rxiao on 6/11/15.
 */
public class LookupRelationShipMethodVisitor extends LocalVariablesSorter implements Opcodes{
    private CodePosition codePosition;
    private boolean isStaticMethod;
    final static String Type_CodePosition = "com/xiaoqq/practise/threadmonitor/relationship/model/CodePosition";
    final static String Type_EventListener = "com/xiaoqq/practise/threadmonitor/relationship/model/EventListener";

    public LookupRelationShipMethodVisitor(MethodVisitor mv, String className, String methodName, String methodDesc, int methodAccess) {
        super(Opcodes.ASM5, methodAccess, methodDesc, mv);
        isStaticMethod = ((methodAccess & Opcodes.ACC_STATIC) != 0);

        codePosition = new CodePosition();
        codePosition.setClassName(className);
        codePosition.setMethodName(methodName);
        codePosition.setMethodDesc(methodDesc);
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.MONITORENTER) {
            handleMonitorEnter(opcode);
            return;
        } else if (opcode == Opcodes.MONITOREXIT) {
            handleMonitorExit(opcode);
            return;
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        try {
            //System.out.println("LookupRelationShipMethodVisitor.visitMethodInsn(opcode:"+opcode +", owner:"+owner + ", name:" + name + ", desc:" + desc);
            if (opcode == Opcodes.INVOKEVIRTUAL) {
                if ("java/lang/Object".equals(owner) && "wait".equals(name)) {//Object.wait(...)
                    handleWait(opcode, owner, name, desc, itf);
                    return ;
                } else if ("java/lang/Object".equals(owner) && ("notify".equals(name) || "notifyAll".equals(name))) {//Object.notify() | Object.notifyAll()
                    handleNotify(opcode, owner, name, desc, itf);
                    return ;
                }
            }
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Handle Event methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void handleMonitorExit(int opcode) {
        System.out.println("LookupRelationShipMethodVisitor.handleMonitorExit()");

        //create local Object by bytecode
        final int varObjindex = this.newLocal(Type.getObjectType("java/lang/Object"));
        //create local Object by bytecode
        mv.visitVarInsn(ASTORE, varObjindex);//Get the Synchronized obj from the top of stack, and store it into local variable "varObjindex".

        mv.visitVarInsn(ALOAD, varObjindex);//Load the obj from local variable to the top of stack.
        //Call the origin instrument
        super.visitInsn(opcode);

        //Create CodePosition Object by bytecode.
        final int varCodePositionindex = createCodePosition();
        mv.visitVarInsn(ALOAD, varObjindex);
        mv.visitVarInsn(ALOAD, varCodePositionindex);
        //Call method EventListener.monitorExit(obj, codePosition);
        mv.visitMethodInsn(
                INVOKESTATIC,
                Type_EventListener,
                EventType.MONITOR_EXIT.getMethodName(),
                getEventMethodDesc(),
                false);
    }

    private void handleMonitorEnter(int opcode) {
        System.out.println("LookupRelationShipMethodVisitor.handleMonitorEnter()");

        //Create CodePosition Object by bytecode.
        final int varCodePositionindex = createCodePosition();

        //create local Object by bytecode
        final int varObjindex = this.newLocal(Type.getObjectType("java/lang/Object"));
        //create local Object by bytecode
        mv.visitVarInsn(ASTORE, varObjindex);//Get the Synchronized obj from the top of stack, and store it into local variable "varObjindex".

        mv.visitVarInsn(ALOAD, varObjindex);
        mv.visitVarInsn(ALOAD, varCodePositionindex);
        //Call method EventListener.beforeMonitorEnter(obj, codePosition);
        mv.visitMethodInsn(
                INVOKESTATIC,
                Type_EventListener,
                EventType.BEFORE_MONITOR_ENTER.getMethodName(),
                getEventMethodDesc(),
                false);

        mv.visitVarInsn(ALOAD, varObjindex);//Load the obj from local variable to the top of stack.
        //Call the origin method
        super.visitInsn(opcode);

        mv.visitVarInsn(ALOAD, varObjindex);
        mv.visitVarInsn(ALOAD, varCodePositionindex);
        //Call method EventListener.afterMonitorEnter(obj, codePosition);
        mv.visitMethodInsn(
                INVOKESTATIC,
                Type_EventListener,
                EventType.AFTER_MONITOR_ENTER.getMethodName(),
                getEventMethodDesc(),
                false);
    }

    private void handleNotify(int opcode, String owner, String name, String desc, boolean itf) {
        System.out.println("LookupRelationShipMethodVisitor.handleNotify(opcode:"+opcode +", owner:"+owner + ", name:" + name + ", desc:" + desc);

        //Create CodePosition Object by bytecode.
        final int varCodePositionindex = createCodePosition();

        //create local Object by bytecode
        final int varObjindex = this.newLocal(Type.getObjectType("java/lang/Object"));
        //create local Object by bytecode
        mv.visitVarInsn(ASTORE, varObjindex);//Get the wait obj from the top of stack, and store it into local variable "varObjindex".

        EventType eventType = "notify".equals(name) ? EventType.BEFORE_NOTIFY : EventType.BEFORE_NOTIFY_ALL;
        mv.visitVarInsn(ALOAD, varObjindex);
        mv.visitVarInsn(ALOAD, varCodePositionindex);
        //Call method EventListener.beforeNotify(obj, codePosition);
        mv.visitMethodInsn(
                INVOKESTATIC,
                Type_EventListener,
                eventType.getMethodName(),
                getEventMethodDesc(),
                false);

        mv.visitVarInsn(ALOAD, varObjindex);//Load the notify obj from local variable to the top of stack.
        //Call the origin method
        super.visitMethodInsn(opcode, owner, name, desc, itf);

        eventType = "notify".equals(name) ? EventType.AFTER_NOTIFY : EventType.AFTER_NOTIFY_ALL;
        mv.visitVarInsn(ALOAD, varObjindex);
        mv.visitVarInsn(ALOAD, varCodePositionindex);
        //Call method EventListener.afterNotify(obj, codePosition);
        mv.visitMethodInsn(
                INVOKESTATIC,
                Type_EventListener,
                eventType.getMethodName(),
                getEventMethodDesc(),
                false);
    }

    private void handleWait(int opcode, String owner, String name, String desc, boolean itf) {
        System.out.println("LookupRelationShipMethodVisitor.handleWait(opcode:"+opcode +", owner:"+owner + ", name:" + name + ", desc:" + desc);

        //Create CodePosition Object by bytecode.
        final int varCodePositionindex = createCodePosition();
        //create local Object by bytecode
        final int varObjindex = this.newLocal(Type.getObjectType("java/lang/Object"));
        if ("()V".equals(desc)) {//void wait()
            insertCodeForBeforeWait(varObjindex, varCodePositionindex);
        } else if ("(J)V".equals(desc)) {//wait(long timeout)
            final int varTimeoutIndex = this.newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, varTimeoutIndex);
            insertCodeForBeforeWait(varObjindex, varCodePositionindex);
            mv.visitVarInsn(LLOAD, varTimeoutIndex);//Load the timeout var from local variable to the top of stack.
        } else if ("(JI)V".equals(desc)) {//wait(long timeout, int nanos) {
            final int varNanosIndex = this.newLocal(Type.INT_TYPE);
            mv.visitVarInsn(ISTORE, varNanosIndex);
            final int varTimeoutIndex = this.newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, varTimeoutIndex);
            insertCodeForBeforeWait(varObjindex, varCodePositionindex);
            mv.visitVarInsn(LLOAD, varTimeoutIndex);//Load the timeout var from local variable to the top of stack.
            mv.visitVarInsn(ILOAD, varNanosIndex);//Load the timeout var from local variable to the top of stack.
        }

        //Call the origin method
        super.visitMethodInsn(opcode, owner, name, desc, itf);

        insertCodeForAfterWait(varObjindex, varCodePositionindex);
    }

    private void insertCodeForBeforeWait(int varObjindex, int varCodePositionindex) {
        mv.visitVarInsn(ASTORE, varObjindex);//Get the wait obj from the top of stack, and store it into local variable "varObjindex".

        mv.visitVarInsn(ALOAD, varObjindex);
        mv.visitVarInsn(ALOAD, varCodePositionindex);
        //Call method EventListener.beforeWait(obj, codePosition);
        mv.visitMethodInsn(
                INVOKESTATIC,
                Type_EventListener,
                EventType.BEFORE_WAIT.getMethodName(),
                getEventMethodDesc(),
                false);

        mv.visitVarInsn(ALOAD, varObjindex);//Load the wait obj from local variable to the top of stack.
    }

    private void insertCodeForAfterWait(int varObjindex, int varCodePositionindex) {
        mv.visitVarInsn(ALOAD, varObjindex);
        mv.visitVarInsn(ALOAD, varCodePositionindex);
        //Call method EventListener.afterWait(obj, codePosition);
        mv.visitMethodInsn(
                INVOKESTATIC,
                Type_EventListener,
                EventType.AFTER_WAIT.getMethodName(),
                getEventMethodDesc(),
                false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Utility methods
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private String getEventMethodDesc() {
        return "(Ljava/lang/Object;L" + Type_CodePosition + ";)V";
    }

    private int createCodePosition() {
        //Create CodePosition Object by bytecode.
        final int varCodePositionindex = this.newLocal(Type.getObjectType(Type_CodePosition));

        //Create CodePosition Object by bytecode.
        mv.visitTypeInsn(NEW, Type_CodePosition);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, Type_CodePosition, "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, varCodePositionindex);

        mv.visitVarInsn(ALOAD, varCodePositionindex);
        mv.visitLdcInsn(codePosition.getClassName());
        mv.visitMethodInsn(INVOKEVIRTUAL, Type_CodePosition, "setClassName", "(Ljava/lang/String;)V", false);

        mv.visitVarInsn(ALOAD, varCodePositionindex);
        mv.visitLdcInsn(codePosition.getMethodName());
        mv.visitMethodInsn(INVOKEVIRTUAL, Type_CodePosition, "setMethodName", "(Ljava/lang/String;)V", false);

        mv.visitVarInsn(ALOAD, varCodePositionindex);
        mv.visitLdcInsn(codePosition.getMethodDesc());
        mv.visitMethodInsn(INVOKEVIRTUAL, Type_CodePosition, "setMethodDesc", "(Ljava/lang/String;)V", false);

        return varCodePositionindex;
    }
}
