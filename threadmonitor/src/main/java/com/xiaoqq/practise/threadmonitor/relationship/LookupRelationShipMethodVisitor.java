package com.xiaoqq.practise.threadmonitor.relationship;

import com.xiaoqq.practise.threadmonitor.relationship.model.CodePosition;
import com.xiaoqq.practise.threadmonitor.relationship.model.EventListener;
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

    public LookupRelationShipMethodVisitor(MethodVisitor mv, String className, String methodName, String methodDesc, int methodAccess) {
        super(Opcodes.ASM5, methodAccess, methodDesc, mv);
        isStaticMethod = ((methodAccess & Opcodes.ACC_STATIC) != 0);

        codePosition = new CodePosition();
        codePosition.setClassName(className);
        codePosition.setMethodName(methodName);
        codePosition.setMethodDesc(methodDesc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        try {
            System.out.println("LookupRelationShipMethodVisitor.visitMethodInsn(opcode:"+opcode +", owner:"+owner + ", name:" + name + ", desc:" + desc);
            if (opcode == Opcodes.INVOKEVIRTUAL) {
                System.out.println("LookupRelationShipMethodVisitor.visitMethodInsn Opcodes.INVOKEVIRTUAL(opcode:"+opcode +", owner:"+owner + ", name:" + name);
                if ("java/lang/Object".equals(owner) && "wait".equals(name)) {//Object.wait(...)
                    if ("()V".equals(desc)) {//void wait()
                        insertCodeForBeforeWait();
                    } else if ("(J)V".equals(desc)) {//wait(long timeout)
                        final int varTimeoutIndex = this.newLocal(Type.LONG_TYPE);
                        mv.visitVarInsn(LSTORE, varTimeoutIndex);
                        insertCodeForBeforeWait();
                        mv.visitVarInsn(LLOAD, varTimeoutIndex);//Load the timeout var from local variable to the top of stack.
                    } else if ("(JI)V".equals(desc)) {//wait(long timeout, int nanos) {
                        final int varNanosIndex = this.newLocal(Type.INT_TYPE);
                        mv.visitVarInsn(ISTORE, varNanosIndex);
                        final int varTimeoutIndex = this.newLocal(Type.LONG_TYPE);
                        mv.visitVarInsn(LSTORE, varTimeoutIndex);
                        insertCodeForBeforeWait();
                        mv.visitVarInsn(LLOAD, varTimeoutIndex);//Load the timeout var from local variable to the top of stack.
                        mv.visitVarInsn(ILOAD, varNanosIndex);//Load the timeout var from local variable to the top of stack.
                    }
                }
            }

            super.visitMethodInsn(opcode, owner, name, desc, itf);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void insertCodeForBeforeWait() {
        final String Type_CodePosition = "com/xiaoqq/practise/threadmonitor/relationship/model/CodePosition";

        //get Obj by bytecode
        final int varObjindex = this.newLocal(Type.getObjectType("java/lang/Object"));
        mv.visitVarInsn(ASTORE, varObjindex);//Get the wait obj from the top of stack, and store it into local variable "varObjindex".

        //Create CodePosition Object by bytecode.
        final int varCodePositionindex = this.newLocal(Type.getObjectType(Type_CodePosition));
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

        mv.visitVarInsn(ALOAD, varObjindex);
        mv.visitVarInsn(ALOAD, varCodePositionindex);

        //Call method EventListener.beforeWait(obj, codePosition);
        mv.visitMethodInsn(
                INVOKESTATIC,
                "com/xiaoqq/practise/threadmonitor/relationship/model/EventListener",
                "beforeWait",
                "(Ljava/lang/Object;L" + Type_CodePosition + ";)V",
                false);

        mv.visitVarInsn(ALOAD, varObjindex);//Load the wait obj from local variable to the top of stack.
    }


}
