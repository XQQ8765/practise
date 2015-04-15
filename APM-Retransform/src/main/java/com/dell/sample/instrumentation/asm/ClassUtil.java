package com.dell.sample.instrumentation.asm;

import org.objectweb.asm.Opcodes;

/**
 * Created by rxiao on 3/31/15.
 */
public class ClassUtil {

    public static String bytecodeClassNameToJavaClassName(String bytecodeClassName) {
        return bytecodeClassName.replaceAll("/", ".");
    }

}
