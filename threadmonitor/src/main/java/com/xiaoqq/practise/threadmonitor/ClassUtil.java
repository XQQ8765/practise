package com.xiaoqq.practise.threadmonitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.ASMifier;
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

    public static void printWithTraceClassVisit(Class clazz) throws IOException {
        printWithTraceClassVisit(clazz.getName());
    }

    private static void printWithTraceClassVisit(ClassReader classReader) {
        ClassVisitor classPrinter = new TraceClassVisitor(new PrintWriter(System.out));
        classReader.accept(classPrinter, 0);
    }

    public static void asmifierWithTraceClassVisit(byte[] classBytes) {
        asmifierWithTraceClassVisit(new ClassReader(classBytes));
    }

    public static void asmifierWithTraceClassVisit(String className) throws IOException {
        asmifierWithTraceClassVisit(new ClassReader(className));
    }

    public static void asmifierWithTraceClassVisit(Class clazz) throws IOException {
        asmifierWithTraceClassVisit(clazz.getName());
    }

    private static void asmifierWithTraceClassVisit(ClassReader classReader) {
        ClassVisitor classPrinter = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
        classReader.accept(classPrinter, 0);
    }

}
