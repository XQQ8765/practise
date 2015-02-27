package com.xqq.asm.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by rxiao on 2/26/15.
 */
public class ClassUtil {
    public static void printWithTraceClassVisit(byte[] classBytes) {
        printWithTraceClassVisit(new ClassReader(classBytes));
    }

    public static void printWithTraceClassVisit(String className) throws IOException {
        printWithTraceClassVisit(new ClassReader(className));
    }

    private static void printWithTraceClassVisit(ClassReader classReader) {
        ClassVisitor classPrinter = new TraceClassVisitor(new PrintWriter(System.out));
        classReader.accept(classPrinter, 0);
    }
}
