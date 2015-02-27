package com.xqq.asm.practise3;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

/**
 * Created by rxiao on 2/27/15.
 */
public class RemoveMethodTransformer extends ClassTransformer{
    private String methodName;
    private String methodDesc;

    public RemoveMethodTransformer(ClassTransformer ct, String methodName, String methodDesc) {
        super(ct);
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public void transform(ClassNode cn) {
        Iterator<MethodNode> methodNodeItr = cn.methods.iterator();
        while (methodNodeItr.hasNext()) {
            MethodNode mn = methodNodeItr.next();
            if (mn.name.equals(methodName) && mn.desc.equals(methodDesc)) {
                methodNodeItr.remove();
                System.out.println("Method " + methodName + " is removed from Class:" + cn.name + ".");
            }
        }
        super.transform(cn);
    }
}
