package com.dell.sample.instrumentation;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.concurrent.atomic.AtomicInteger;

import com.dell.sample.instrumentation.asm.ClassUtil;
import com.dell.sample.instrumentation.asm.PollWebSiteServletClassVisitor;
import com.dell.sample.instrumentation.asm.PollerClassVisitor;
import com.dell.sample.instrumentation.asm.PollingServiceClassVisitor;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import static com.dell.sample.instrumentation.asm.IConstant.CLASS_NAME_POLLER;
import static com.dell.sample.instrumentation.asm.IConstant.CLASS_NAME_POLLING_SERVICE;
import static com.dell.sample.instrumentation.asm.IConstant.CLASS_NAME_POLL_WEB_SITE_SERVLET;

public class EmbededProfilingAgent implements ClassFileTransformer {

    public static AtomicInteger transformedClasses = new AtomicInteger();

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        transformedClasses.addAndGet(1);

        if (CLASS_NAME_POLL_WEB_SITE_SERVLET.equals(ClassUtil.bytecodeClassNameToJavaClassName(className))) {
            System.out.println("EmbededProfilingAgent is going to transform PollWebSiteServlet");
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            ClassVisitor cv = new PollWebSiteServletClassVisitor(cw, loader);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);

            classfileBuffer = cw.toByteArray();

            //TODO: remove it
            try {
                FileUtils.writeByteArrayToFile(new File("d:\\workspace\\tmp\\generatedclasses\\" + className + "_embeded.class"), classfileBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return classfileBuffer;

        } else if (CLASS_NAME_POLLER.equals(ClassUtil.bytecodeClassNameToJavaClassName(className))) {
            System.out.println("EmbededProfilingAgent is going to transform Poller");
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            ClassVisitor cv = new PollerClassVisitor(cw, loader);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            classfileBuffer = cw.toByteArray();

            //TODO: remove it
            saveTransformedClassBytes(className, classfileBuffer);

            return classfileBuffer;
        } else if (CLASS_NAME_POLLING_SERVICE.equals(ClassUtil.bytecodeClassNameToJavaClassName(className))) {
            System.out.println("EmbededProfilingAgent is going to transform PollingService");
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            ClassVisitor cv = new PollingServiceClassVisitor(cw, loader);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            classfileBuffer = cw.toByteArray();

            //TODO: remove it
            saveTransformedClassBytes(className, classfileBuffer);

            return classfileBuffer;
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
	} */

}
