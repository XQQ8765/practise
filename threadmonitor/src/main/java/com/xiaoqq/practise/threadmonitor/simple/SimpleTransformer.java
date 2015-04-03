package com.xiaoqq.practise.threadmonitor.simple;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.objectweb.asm.*;

/**
 *  http://www.javalobby.org/java/forums/m91837350.html
 */
public class SimpleTransformer implements ClassFileTransformer {

    public SimpleTransformer() {
        super();
    }

    public static void premain(String arg, Instrumentation instrumentation) {
        System.out.println("SimpleTransformer: Start to create Agent");
        SimpleTransformer agent = new SimpleTransformer();
        instrumentation.addTransformer(agent, true);
        /*
        Class[] allClasses = instrumentation.getAllLoadedClasses();
        for (Class c : allClasses) {
            if (!c.isInterface() && instrumentation.isModifiableClass(c)) {
                try {
                    instrumentation.retransformClasses(c);
                } catch (Exception e) {
                    System.err.println("Cannot retransform class. Exception: " + e);
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }*/
    }
    public byte[] transform(ClassLoader loader, String className, Class redefiningClass, ProtectionDomain domain, byte[] bytes) throws IllegalClassFormatException {
        //System.out.println("Modifying: " + className);
        byte[] result = bytes;
        try {
            // Create a reader for the existing bytes.
            ClassReader reader = new ClassReader(bytes);
            // Create a writer
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
            // Create our class adapter, pointing to the class writer
            // and then tell the reader to notify our visitor of all
            // bytecode instructions
            reader.accept(new PrintStatementClassAdapter(writer, className), ClassReader.SKIP_DEBUG);
            // get the result from the writer.
            result = writer.toByteArray();
        }
        // Runtime exceptions thrown by the above code disappear
        // This catch ensures that they are at least reported.
        catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * A simple class adapter that wraps the visitMethod result to return out
     * method visitor implementation.
     */
    private class PrintStatementClassAdapter extends ClassVisitor {
        private String className;
        PrintStatementClassAdapter(ClassVisitor visitor, String theClass) {
            super(Opcodes.ASM4, visitor);
            className = theClass;
        }

        @Override public MethodVisitor visitMethod(int arg0, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv =super.visitMethod(arg0, name, descriptor, signature, exceptions);
            return new PrintStatementMethodAdapter(mv, className, name, descriptor);
        }
    }

    /**
     * A method visitor implementation that injects our system print statement
     * at the top of the method.
     */
    private class PrintStatementMethodAdapter extends MethodVisitor {
        private String methodName;
        private String methodDescriptor;
        private String className;

        PrintStatementMethodAdapter(MethodVisitor visitor, String theClass, String name, String descriptor) {
            super(Opcodes.ASM4, visitor);
            methodName = name;
            methodDescriptor = descriptor;
            className = theClass;
        }

        @Override public void visitCode() {
            // trigger the super class
            super.visitCode();

            // load the system.out field into the stack
            super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            // load the constant string we want to print into the stack
            // this string is created by the values we get from ASM
            super.visitLdcInsn("Method " + className + "." + methodName + "(" + methodDescriptor + ") called");
            // trigger the method instruction for 'println'
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");

        }
    }
}
