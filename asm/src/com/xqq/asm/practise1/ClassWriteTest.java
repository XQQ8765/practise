package com.xqq.asm.practise1;

import org.objectweb.asm.ClassReader;

/**
 * Created by rxiao on 1/30/15.
 */
public class ClassWriteTest {
    public static void main(String[] args) {
        byte[] classBytes = new MyClassWriter().writeClass();

        ClassPrinter classPrinter = new ClassPrinter();
        ClassReader classReader = new ClassReader(classBytes);
        classReader.accept(classPrinter, 0);
    }
}
