package com.xqq.asm.practise1;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.*;

/**
 * Created by rxiao on 1/30/15.
 */
public class ClassPrinter extends ClassVisitor {

    public ClassPrinter() {
        super(Opcodes.ASM5);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        StringBuilder sb = new StringBuilder();
        sb.append("Version: " + version);
        sb.append("\n");
        sb.append("Access: " + access);
        sb.append("\n");
        sb.append("Class: " + name + " extends " + superName);
        if (interfaces != null && interfaces.length > 0) {
            sb.append(" implements " + StringUtils.join(interfaces, ","));
        }
        sb.append(" {");
        System.out.println(sb.toString());
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        System.out.println("\t\tField: " + desc + " " + name);
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t\tMethod: " + desc + " " + name);
        if (exceptions != null && exceptions.length > 0) {
            sb.append(" " + StringUtils.join(exceptions, ","));
        }
        System.out.println(sb.toString());
        return null;
    }

    @Override
    public void visitEnd() {
        System.out.println("}\n");
    }
}
