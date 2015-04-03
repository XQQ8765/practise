package com.xiaoqq.practise.threadmonitor.util;

import org.objectweb.asm.Opcodes;

/**
 * Created by rxiao on 3/31/15.
 */
public class MonitorUtil {
    public static final int ASM_API = Opcodes.ASM4;
    public static String THREAD_TYPE_BYTECODE_NAME = "java/lang/Thread";
    public static String RUNNABLE_TYPE_BYTECODE_NAME = "java/lang/Runnable";
    public static String CALLABLE_TYPE_BYTECODE_NAME = "java/util/concurrent/Callable";

    public static String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

    public static String getObjectId(Object obj) {
        if (obj == null) {
            return "null";
        }
        return "Object:" + obj + ", Type:" + obj.getClass() + ", identityCode: " +System.identityHashCode(obj);
    }

    public static boolean isAssignableFrom(String super_bytecode_type_name, String child_bytecode_type_name , ClassLoader classLoader) {
        try {
            Class clazz1 = Class.forName(bytecodeClassNameToJavaClassName(super_bytecode_type_name), false, classLoader);
            Class clazz2 = Class.forName(bytecodeClassNameToJavaClassName(child_bytecode_type_name), false, classLoader);
            return clazz1.isAssignableFrom(clazz2);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String bytecodeClassNameToJavaClassName(String bytecodeClassName) {
        return bytecodeClassName.replaceAll("/", ".");
    }

    /**
     * Determine whether the bytecode_type_name is "Thread", "Runnable" or "Callable".
     * @param bytecode_type_name String, for example: java/lang/Object
     * @return boolean
     */
    public static boolean isThreadTypeOrInterfaces(String bytecode_type_name, ClassLoader classLoader) {
        if (isAssignableFrom(THREAD_TYPE_BYTECODE_NAME, bytecode_type_name, classLoader)) {
            return true;
        }
        if (isAssignableFrom(RUNNABLE_TYPE_BYTECODE_NAME, bytecode_type_name, classLoader)) {
            return true;
        }
        if (isAssignableFrom(CALLABLE_TYPE_BYTECODE_NAME, bytecode_type_name, classLoader)) {
            return true;
        }
        return false;
    }
}
