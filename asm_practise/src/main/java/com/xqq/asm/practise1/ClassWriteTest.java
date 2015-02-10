package com.xqq.asm.practise1;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;

/**
 * Created by rxiao on 1/30/15.
 */
public class ClassWriteTest {
    private static class MyClassLoader extends ClassLoader {
        public Class defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

    public static void main(String[] args) {
        byte[] classBytes = new MyClassWriter().writeClass();

        //ClassVisitor classPrinter = new ClassPrinter();

        ClassVisitor classPrinter = new TraceClassVisitor(new PrintWriter(System.out));
        ClassReader classReader = new ClassReader(classBytes);
        classReader.accept(classPrinter, 0);

        MyClassLoader myClassLoader = new MyClassLoader();
        Class beanClazz = myClassLoader.defineClass("org.xqq.test.Comparable", classBytes);
    }
}
