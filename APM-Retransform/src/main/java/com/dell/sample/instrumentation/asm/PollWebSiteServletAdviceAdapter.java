package com.dell.sample.instrumentation.asm;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;
import static com.dell.sample.instrumentation.asm.IConstant.ASM_API;

/**
 * Created by rxiao on 4/15/15.
 */
public class PollWebSiteServletAdviceAdapter  extends AdviceAdapter {
    private boolean isRetransformAgent;
    private int agentId;

    protected PollWebSiteServletAdviceAdapter(MethodVisitor mv, int access, String name, String desc) {
        super(ASM_API, mv, access, name, desc);
        this.isRetransformAgent = false;
    }

    protected PollWebSiteServletAdviceAdapter(MethodVisitor mv, int access, String name, String desc, int agentId) {
        super(ASM_API, mv, access, name, desc);
        this.isRetransformAgent = true;
        this.agentId = agentId;
    }

    @Override
    protected void onMethodExit(int opcode) {

        /*
        Gearate Code for "EmbededProfilingAgent":
        pointcutMethod.insertAfter("\n{System.out.println(\"response code:\" + $2.getStatus()+\",\"+this);}\n");

        Gearate Code for "RetransformAgent":
        pointcutMethod.insertAfter("\n{System.out.println(\"response code:" + agentId + ":\" + $2.getStatus()+\",\"+this);}\n");
        */


        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);


        if (isRetransformAgent) {
            mv.visitLdcInsn("response code:"+agentId+":");
        } else {
            mv.visitLdcInsn("response code:");
        }

        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEINTERFACE, "javax/servlet/http/HttpServletResponse", "getStatus", "()I", true);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
        mv.visitLdcInsn(",");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        super.onMethodExit(opcode);
    }
}
