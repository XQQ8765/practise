package com.xqq.asm.mergetwoclasses;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingFieldAdapter;
import org.objectweb.asm.commons.RemappingMethodAdapter;
import org.objectweb.asm.tree.*;

import java.util.List;

/**
 * Created by rxiao on 3/12/15.
 */
public class MergeClassNode extends ClassNode {
    private String replacedClassName;

    public MergeClassNode() {
        super(Opcodes.ASM4);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (this.version == 0) {
            this.version = version;
        }

        if ((access & Opcodes.ACC_INTERFACE) != 0) {//It's a interface
            if (this.access == 0) {
                throw new IllegalStateException("An Interface should not be listed in the first place.");
            } else {
                addInterface(name);//Implement this interface
            }
        } else {
            if (this.access == 0) {
                this.access = access;
            }
        }

        if (StringUtils.isBlank(this.name)) {
            this.name = name;
        } else {
            replacedClassName = name;//The class2's name should be replaced with class1's name.
        }
        if (StringUtils.isBlank(this.superName)) {
            this.superName = superName;
        }
        if (interfaces != null) {
            for (String _interface: interfaces) {
                addInterface(_interface);
            }
        }
    }

    private void addInterface(String _interface) {
        if (!this.interfaces.contains(_interface)) {
                    this.interfaces.add(_interface);
                }
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        if (StringUtils.isBlank(owner)) {
            outerClass = owner;
        }
        if (StringUtils.isBlank(name)) {
            outerMethod = name;
        }
        if (StringUtils.isBlank(desc)) {
            outerMethodDesc = desc;
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (visibleAnnotations != null) {
            for (AnnotationNode annotationNode : (List<AnnotationNode>) visibleAnnotations) {
                if (desc != null && desc.equals(annotationNode.desc)) {
                    //Already contains this AnnotationNode, return
                    return null;
                }
            }
        }
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        if (visibleTypeAnnotations != null) {
            for (TypeAnnotationNode typeAnnotation : (List<TypeAnnotationNode>) visibleTypeAnnotations) {
                if (desc != null && desc.equals(typeAnnotation.desc) && (typeRef == typeAnnotation.typeRef) && (typePath == typeAnnotation.typePath)) {
                    //Already contains this AnnotationNode, return
                    return null;
                }
            }
        }
        return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        if (attrs != null && attrs.contains(attr)) {
            return ;
        }
        super.visitAttribute(attr);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        if (innerClasses != null) {
            for (InnerClassNode icn : (List<InnerClassNode>) innerClasses) {
                if ((name != null && name.equals(icn.name)) && (innerName != null && innerName.equals(icn.innerName))) {
                    //Already contains this InnerClassNode, return
                    return ;
                }
            }
        }
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        for (FieldNode fn : (List<FieldNode>) fields) {
            if ((name != null && name.equals(fn.name)) && (desc != null && desc.equals(fn.desc))) {
                //Already contains this field, return
                return null;
            }
        }

        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        for (MethodNode mn : (List<MethodNode>) methods) {
            if ((name != null && name.equals(mn.name)) && (desc != null && desc.equals(mn.desc))) {
                //Already contains this method, return
                return null;
            }
        }

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new RemappingMethodAdapter(access, desc, mv, remapper);
    }

    //Replace the classnode im method instructions
    private Remapper remapper = new Remapper() {
        @Override
        public String map(String typeName) {
            if (typeName.equals(replacedClassName)) {
                return MergeClassNode.this.name;
            }
            return super.map(typeName);
        }
    } ;
}
