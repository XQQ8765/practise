package com.dell.sample.instrumentation;

import com.dell.sample.instrumentation.asm.ClassUtil;
import com.dell.sample.instrumentation.asm.PollWebSiteServletClassVisitor;
import com.dell.sample.instrumentation.asm.PollerClassVisitor;
import com.dell.sample.instrumentation.asm.PollingServiceClassVisitor;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.concurrent.atomic.AtomicInteger;

import static com.dell.sample.instrumentation.asm.IConstant.*;

public class RetransformAgent implements ClassFileTransformer {
    public static AtomicInteger transformedClasses = new AtomicInteger();

    private int agentId;

    public RetransformAgent(int agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return "" + agentId;
    }

    @Override
    public String toString() {
        return "" + agentId;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        transformedClasses.addAndGet(1);

        if (CLASS_NAME_POLL_WEB_SITE_SERVLET.equals(ClassUtil.bytecodeClassNameToJavaClassName(className))) {
            if (agentId % 2 == 0) {
                System.out.println("RetransformAgent is going to transform PollWebSiteServlet");
                ClassReader cr = new ClassReader(classfileBuffer);
                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
                ClassVisitor cv = new PollWebSiteServletClassVisitor(cw, loader, agentId);
                cr.accept(cv, ClassReader.EXPAND_FRAMES);
                classfileBuffer = cw.toByteArray();

                //saveTransformedClassBytes(className, classfileBuffer);

                return classfileBuffer;
            } else {
                System.out.println("RetransformAgent is going to transform PollWebSiteServlet, remove instrumentation!");
            }
        } else if (CLASS_NAME_POLLER.equals(ClassUtil.bytecodeClassNameToJavaClassName(className))) {
            if (agentId % 2 == 0) {
                System.out.println("RetransformAgent is going to transform Poller");
                ClassReader cr = new ClassReader(classfileBuffer);
                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
                ClassVisitor cv = new PollerClassVisitor(cw, loader, agentId);
                cr.accept(cv, ClassReader.EXPAND_FRAMES);
                classfileBuffer = cw.toByteArray();

                //saveTransformedClassBytes(className, classfileBuffer);

                return classfileBuffer;
            } else {
                System.out.println("RetransformAgent is going to transform Poller, remove instrumentation!");
            }
        } else if (CLASS_NAME_POLLING_SERVICE.equals(ClassUtil.bytecodeClassNameToJavaClassName(className))) {
            if (agentId % 2 == 0) {
                System.out.println("RetransformAgent is going to transform PollingService");
                ClassReader cr = new ClassReader(classfileBuffer);
                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
                ClassVisitor cv = new PollingServiceClassVisitor(cw, loader, agentId);
                cr.accept(cv, ClassReader.EXPAND_FRAMES);
                classfileBuffer = cw.toByteArray();

                //saveTransformedClassBytes(className, classfileBuffer);

                return classfileBuffer;
            } else {
                System.out.println("RetransformAgent is going to transform PollingService, remove instrumentation!");
            }
        }

        return classfileBuffer;
    }

    private static void saveTransformedClassBytes(String className, byte[] bytes) {
        //TODO: remove it
        try {
            FileUtils.writeByteArrayToFile(new File("d:\\workspace\\tmp\\generatedclasses\\" + className + "_embeded.class"), bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        /*try {
            CtClass cl = null;
            if ("com/dell/apm/target/monitor/app/ping/PollWebSiteServlet".equals(className)) {
                if (agentId % 2 == 0) {
                    cl = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
                    System.out.println("RetransformAgent is going to transform PollWebSiteServlet");
                    CtMethod pointcutMethod = findPointcut(cl, "doPost");
                    pointcutMethod.insertAfter("\n{System.out.println(\"response code" + agentId + ":\" + $2.getStatus()+\",\"+this);}\n");
                } else {
                    System.out.println("RetransformAgent is going to transform PollWebSiteServlet, remove instrumentation!");
                }
            }
            if ("com/dell/apm/target/monitor/app/ping/Poller".equals(className)) {
                if (agentId % 2 == 0) {
                    cl = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
                    System.out.println("RetransformAgent is going to transform Poller");
                    CtMethod pointcutMethod = findPointcut(cl, "poll");
                    pointcutMethod.insertAfter("\n{System.out.println(\"poll urls" + agentId + ":\" + $1+\",\"+this);}\n");
                } else {
                    System.out.println("RetransformAgent is going to transform Poller, remove instrumentation!");
                }
            }
            if ("com/dell/apm/target/monitor/app/ping/PollingService".equals(className)) {
                if (agentId % 2 == 0) {
                    cl = pool.makeClass(new
                            java.io.ByteArrayInputStream(classfileBuffer));
                    System.out.println("RetransformAgent is going to transform PollingService");
                    CtMethod pointcutMethod = findPointcut(cl, "ping");
                    pointcutMethod.insertAfter("\n{System.out.println(\"finished ping url2:\" + $1+\",\"+this);}\n");
                } else {
                    System.out.println("RetransformAgent is going to transform PollingService, remove instrumentation!");
                }
            }
            if (cl != null)
                cl.detach();
            return cl != null ? cl.toBytecode() : classfileBuffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classfileBuffer;

    }*/

    /*
    private CtMethod findPointcut(CtClass cl, String methodName) {
        CtMethod pointcut = null;
        CtMethod[] behaviors = cl.getDeclaredMethods();
        for (CtMethod behavior : behaviors) {
            if (methodName.equals(behavior.getName())) {
                pointcut = behavior;
            }
        }
        return pointcut;
    }
     */
}
