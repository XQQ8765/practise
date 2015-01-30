package com.xqq.asm.practise1;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import java.io.IOException;

/**
 * Created by rxiao on 1/30/15.
 */
public class ClassPrinterTest {
    public static void main(String[] args) throws IOException {
        visitClass("com.xqq.asm.practise1.ClassPrinter");
        visitClass("java.io.CharArrayReader");
        visitClass("java.io.FileInputStream");
        visitClass("java.lang.Comparable");
    }

    private static void visitClass(String className)  throws IOException {
        ClassPrinter classPrinter = new ClassPrinter();
        ClassReader classReader = new ClassReader(className);
        classReader.accept(classPrinter, 0);
    }
}
