package com.xqq.asm.introduceInterface;

import org.objectweb.asm.tree.ClassNode;

/**
 * Created by rxiao on 2/27/15.
 */
public abstract class ClassTransformer {
    private static final String BEAN = "com/xqq/asm/introduceInterface/C";
    protected ClassTransformer ct;

    public ClassTransformer(ClassTransformer ct) {
        this.ct = ct;
    }

    public void transform(ClassNode cn) {
        if (ct != null) {
            ct.transform(cn);
        }
    }
}
