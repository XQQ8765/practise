package com.dell.sample.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.concurrent.atomic.AtomicInteger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class RetransformAgent implem`ents ClassFileTransformer {

    public static ClassPool pool = ClassPool.getDefault();
    public static AtomicInteger transformedClasses = new AtomicInteger();

    private int agentId;

    public RetransformAgent(int agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return ""+agentId;
    }

    @Override
    public String toString() {
        return ""+agentId;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        transformedClasses.addAndGet(1);
        try {
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
    }

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

}
