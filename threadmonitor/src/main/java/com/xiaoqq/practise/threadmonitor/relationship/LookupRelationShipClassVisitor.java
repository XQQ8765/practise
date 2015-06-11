package com.xiaoqq.practise.threadmonitor.relationship;

import com.xiaoqq.practise.threadmonitor.util.MonitorUtil;
import com.xiaoqq.practise.threadmonitor.uuid.ThreadConstructAdviceAdapter;
import com.xiaoqq.practise.threadmonitor.uuid.ThreadRunningAdviceAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static com.xiaoqq.practise.threadmonitor.util.IConstant.METHOD_BEFORE_RUN;
import static com.xiaoqq.practise.threadmonitor.util.IConstant.METHOD_POST_CONSTRUCTOR;
import static org.objectweb.asm.Opcodes.*;

/**
 * Created by rxiao on 3/31/15.
 */
public class LookupRelationShipClassVisitor extends ClassVisitor {
    private String className;
    private int api;
    private ClassLoader classLoader;

    public LookupRelationShipClassVisitor(int api, ClassVisitor cv, ClassLoader classLoader) {
        super(api, cv);
        this.api = api;
        this.classLoader = classLoader;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (((access & Opcodes.ACC_ABSTRACT) != 0)
                || ((access & Opcodes.ACC_BRIDGE) != 0))  {
            return mv;
        }
        return new LookupRelationShipMethodVisitor(mv, className, name, desc, access);
    }
}
