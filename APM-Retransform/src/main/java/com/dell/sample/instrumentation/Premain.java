package com.dell.sample.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class Premain {

	public static List<ClassFileTransformer> registeredTransformers = new ArrayList<ClassFileTransformer>();

	public static void premain(String agentArgs, final Instrumentation inst) {
		System.out.println("run premain");
		final ClassFileTransformer trans = new EmbededProfilingAgent();
		inst.addTransformer(trans, true);
		registeredTransformers.add(trans);
		Thread logger = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ClassFileTransformer currentTransformer = trans;
					int i = 0;
					while (true) {
						synchronized (trans) {
							try {
								trans.wait(20000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						System.out.println("I am going to remove the old transformer " + currentTransformer
								+ " , and add the retransformer agent");
						inst.removeTransformer(currentTransformer);
						currentTransformer = new RetransformAgent(i++);
						inst.addTransformer(currentTransformer, true);
						long startTime = System.currentTimeMillis();
						for (Class clz : inst.getAllLoadedClasses()) {
							if (inst.isModifiableClass(clz) && isNecessaryRetransform(clz)) {
								inst.retransformClasses(clz);
							}
						}
						System.out.println("retransform all classes done, used " + (System.currentTimeMillis() - startTime) + " ms");
						System.out.println("EmbededProfilingAgent.transformedClasses:" + EmbededProfilingAgent.transformedClasses);
						System.out.println("RetransformAgent.transformedClasses:" + RetransformAgent.transformedClasses);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		logger.start();
	}

	static boolean isNecessaryRetransform(Class clz) {
		return "PollWebSiteServlet".equals(clz.getSimpleName()) || "Poller".equals(clz.getSimpleName()) || "PollingService".equals(clz.getSimpleName());
	}


}
