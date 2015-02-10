package com.xqq.asm.practise1;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.IOException;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by rxiao on 2/2/15.
 */
public class ChangeVersionAdapterTest {
    public static void main(String[] args) throws IOException {
        //String className = "java.io.CharArrayReader";
        String className = "java.util.Arrays";
        visitClass(className);
        compareOptimization(className);
    }

    private static void compareOptimization(String className)  throws IOException {
        long LOOP = 10L;
        long t0 = System.currentTimeMillis();
        for (int i=0; i<LOOP; ++i) {
            transform(className, false);
        }
        long spentTime0 = System.currentTimeMillis() - t0;

        t0 = System.currentTimeMillis();
        for (int i=0; i<LOOP; ++i) {
            transform(className, true);
        }
        long spentTime1 = System.currentTimeMillis() - t0;

        System.out.println("LOOP:" + LOOP
                + ", without optimization:" + spentTime0 + "ms"
                + ", with optimization:" + spentTime1 + "ms");
    }

    private static void visitClass(String className)  throws IOException {
        System.out.println("###################Before transform#####################");
        ClassVisitor classPrinter = new ClassPrinter();
        ClassReader classReader = new ClassReader(className);
        classReader.accept(classPrinter, 0);

        byte[] bytesAfterTransform = transform(className, true);

        System.out.println("###################After transform#####################");
        classReader = new ClassReader(bytesAfterTransform);
        classReader.accept(classPrinter, 0);
    }

    private static byte[] transform(String className, boolean optimization) throws IOException{
        ClassReader classReader = new ClassReader(className);

        //Determine whether do a optimization for classWriter
        ClassWriter classWriter = optimization ? new ClassWriter(classReader, 0) : new ClassWriter(0);
        //Change the version of the class
        ClassVisitor adapterClassVisitor = new ChangeVersionAdapter(classWriter);
        classReader.accept(adapterClassVisitor, 0);

        return  classWriter.toByteArray();
    }
}
