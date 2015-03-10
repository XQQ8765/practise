package com.xqq.asm.rename;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import java.util.HashMap;
import java.util.Map;
import static org.objectweb.asm.Opcodes.ASM4;

/**
 * Created by rxiao on 3/10/15.
 */
public class RenameFieldMethodClassVisitor extends ClassVisitor{
    private Map<String, String> renameMethodMap;
    private Map<String, String> renameFieldMap;

    public RenameFieldMethodClassVisitor(ClassVisitor cv) {
        super(ASM4, cv);

        renameFieldMap = new HashMap<String, String>(){};
        renameFieldMap.put("v", "k");

        renameMethodMap = new HashMap<String, String>(){};
        renameMethodMap.put("getV", "getK");
        renameMethodMap.put("setV", "setK");
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, getReplacedFieldName(name), desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, getReplacedMethodName(name), desc, signature, exceptions);
        return new RenameMethodVisitor(mv);
    }

    private String getReplacedFieldName(String name) {
        if (renameFieldMap.containsKey(name)) {
            return renameFieldMap.get(name);
        }
        return name;
    }

    private String getReplacedMethodName(String name) {
        if (renameMethodMap.containsKey(name)) {
            return renameMethodMap.get(name);
        }
        return name;
    }

    private class RenameMethodVisitor extends MethodVisitor {

        public RenameMethodVisitor(MethodVisitor cv) {
            super(ASM4, cv);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            super.visitFieldInsn(opcode, owner, getReplacedFieldName(name), desc);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            super.visitMethodInsn(opcode, owner, getReplacedMethodName(name), desc, itf);
        }
    }



}
