package com.xqq.asm.practise1;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by rxiao on 2/2/15.
 */
public class ASMifierTest {
    public static void main(String[] args) throws IOException {
        String className = "java.lang.String";

        //ClassVisitor classPrinter = new ClassPrinter();

        //ClassVisitor classPrinter = new TraceClassVisitor(new PrintWriter(System.out));
        ClassVisitor classPrinter = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
        ClassReader classReader = new ClassReader(className);
        classReader.accept(classPrinter, 0);
    }
}
