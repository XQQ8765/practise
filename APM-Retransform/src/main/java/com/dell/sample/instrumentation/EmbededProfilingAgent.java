package com.dell.sample.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.concurrent.atomic.AtomicInteger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class EmbededProfilingAgent implements ClassFileTransformer {

	public static AtomicInteger transformedClasses = new AtomicInteger();

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		transformedClasses.addAndGet(1);
		try {
			CtClass cl = pool.makeClass(new java.io.ByteArrayInputStream(classfileBuffer));
			if ("com.dell.apm.target.monitor.app.ping.PollWebSiteServlet".equals(cl.getName())) {
				System.out.println("EmbededProfilingAgent is going to transform PollWebSiteServlet");
				CtMethod pointcutMethod = findPointcut(cl, "doPost");
				pointcutMethod.insertAfter("\n{System.out.println(\"response code:\" + $2.getStatus()+\",\"+this);}\n");
			}
			if ("com.dell.apm.target.monitor.app.ping.Poller".equals(cl.getName())) {
				System.out.println("EmbededProfilingAgent is going to transform Poller");
				CtMethod pointcutMethod = findPointcut(cl, "poll");
				pointcutMethod.insertAfter("\n{System.out.println(\"poll urls:\" + $1+\",\"+this);}\n");
			}
			if ("com.dell.apm.target.monitor.app.ping.PollingService".equals(cl.getName()))
			{
                System.out.println("EmbededProfilingAgent is going to transform PollingService");
                CtMethod pointcutMethod = findPointcut(cl, "ping");
                pointcutMethod.insertAfter("\n{System.out.println(\"finished ping url:\" + $1+\",\"+this);}\n");
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
