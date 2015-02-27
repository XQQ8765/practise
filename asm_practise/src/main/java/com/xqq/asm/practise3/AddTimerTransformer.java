package com.xqq.asm.practise3;

import com.sun.org.apache.bcel.internal.generic.ATHROW;
import com.sun.org.apache.bcel.internal.generic.IRETURN;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;
import java.util.Iterator;
import java.util.List;

/**
 * Created by rxiao on 2/27/15.
 */
public class AddTimerTransformer extends ClassTransformer{
    public AddTimerTransformer(ClassTransformer ct) {
        super(ct);
    }

    @Override
    public void transform(ClassNode cn) {
        //Add field "public static long timer"
        cn.fields.add(new FieldNode(ACC_PUBLIC + ACC_STATIC, "timer", "J", null, null));

        for (MethodNode mn: (List<MethodNode>) cn.methods) {
            if ("<init>".equals(mn.name) || "<clinit>".equals(mn.name)) {
                continue;
            }
            InsnList insnList = mn.instructions;

            Iterator<AbstractInsnNode> itr = insnList.iterator();
            while (itr.hasNext()) {
                AbstractInsnNode insnNode = itr.next();
                int op = insnNode.getOpcode();
                //Generate code at the end of method: timer += System.currentTimeMillis();
                if (((op >= IRETURN) && (op <= RETURN)) || op == ATHROW) {
                    InsnList tmpInsnList = new InsnList();
                    tmpInsnList.add(new FieldInsnNode(GETSTATIC, cn.name, "timer", "J"));
                    tmpInsnList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false));
                    tmpInsnList.add(new InsnNode(LADD));
                    tmpInsnList.add(new FieldInsnNode(PUTSTATIC, cn.name, "timer", "J"));

                    insnList.insert(insnNode.getPrevious(), tmpInsnList);
                }
            }

            //Insert code to the begining of method: timer -= System.currentTimeMillis();
            InsnList il = new InsnList();
            il.add(new FieldInsnNode(GETSTATIC, cn.name, "timer", "J"));
            il.add(new MethodInsnNode(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false));
            il.add(new InsnNode(LSUB));
            il.add(new FieldInsnNode(PUTSTATIC, cn.name, "timer", "J"));
            insnList.insert(il);

            mn.maxStack += 4;
        }

        super.transform(cn);
    }
}
