package com.xqq.asm.mergetwoclasses;

import org.objectweb.asm.ClassVisitor;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingMethodAdapter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

/**
 * Created by rxiao on 3/13/15.
 */
public class MergeClass2Visitor extends ClassVisitor {
    private ClassNode classNode;
    private String mergedClassName;

    public MergeClass2Visitor(ClassNode classNode) {
        super(ASM4);
        this.classNode = classNode;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.mergedClassName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitEnd() {
        for (Iterator it = classNode.fields.iterator(); it.hasNext(); ) {
            FieldNode fieldNode = (FieldNode) it.next();
            fieldNode.accept(this);
        }
        for (Iterator it = classNode.methods.iterator(); it.hasNext(); ) {
            MethodNode methodNode = (MethodNode) it.next();
            String[] exceptions = new String[methodNode.exceptions.size()];
            methodNode.exceptions.toArray(exceptions);

            MethodVisitor mv = super.visitMethod(
                    methodNode.access,
                    methodNode.name,
                    methodNode.desc,
                    methodNode.signature,
                    exceptions);
            methodNode.instructions.resetLabels();
            methodNode.accept(
                    new RemappingMethodAdapter(
                            methodNode.access,
                            methodNode.desc,
                            mv,
                            new Remapper() {
                                @Override
                                public String map(String typeName) {
                                    if (typeName.equals(classNode.name)) {
                                        return mergedClassName;
                                    }
                                    return super.map(typeName);
                                }
                            }));
        }
        super.visitEnd();
    }
}
