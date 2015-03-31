/* Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiaoqq.practise.threadmonitor;

import org.objectweb.asm.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class Agent implements ClassFileTransformer {

  // Ignore list to eliminate endless recursion.
  private static String[] ignore = new String[]{
      "com/xiaoqq/practise/threadmonitor",
      "sun",
      "java",
      "com/intellij",

      // Classes required by EventListener itself. Triggering events in these will
      // cause endless recursion.
      "java/io/PrintWriter",
      "java/lang/AbstractStringBuilder",
      "java/lang/Boolean",
      "java/lang/Class",
      "java/lang/Long",
      "java/lang/String",
      "java/lang/StringBuilder",
      "java/lang/System",

      // Exclusions to workaround HotSpot internal failures.
      "java/io/",
      "java/lang/Thread",
      "java/lang/ref/",
      "java/lang/reflect/",
      "java/nio/",
      "java/util/Arrays",

      // Exclude some internals of java.util.concurrent to avoid false report.
      // TimeUnit is enum. TimeUnit<init>:71 provoke false positive in tryLock test.
      "java/util/concurrent/TimeUnit",
      // ReentrantReadWriteLock$Sync provoke false positive in tryLock and readAndWriteLocks tests.
      "java/util/concurrent/locks/ReentrantReadWriteLock",
      // AbstractQueuedSynchronizer$ConditionObject provoke false positive in cyclicBarrier test.
      "java/util/concurrent/locks/AbstractQueuedSynchronizer",
  };

  // A list of exceptions for the ignore list.
  private static String[] noignore = new String[]{};

  public static void premain(String arg, Instrumentation instrumentation) {
    System.out.println("Start to create Agent");
    Agent agent = new Agent();
    instrumentation.addTransformer(agent, true);

      Class[] allClasses = instrumentation.getAllLoadedClasses();
      for (Class c : allClasses) {
        if (!c.isInterface() && instrumentation.isModifiableClass(c)) {
          try {
            instrumentation.retransformClasses(c);
          } catch (Exception e) {
            System.err.println("Cannot retransform class. Exception: " + e);
              e.printStackTrace();
              System.exit(1);
          }
        }
      }
  }

  private boolean inIgnoreList(String className) {
    for (String anIgnore : ignore) {
      if (className.startsWith(anIgnore)) {
        for (String aNoignore : noignore) {
          if (className.startsWith(aNoignore)) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }

  public byte[] transform(ClassLoader loader, String className,
                          Class clazz, java.security.ProtectionDomain domain, byte[] bytes) {
      if (inIgnoreList(className)) {
        return bytes;
      }

      ClassReader cr = new ClassReader(bytes);
      ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
      ClassVisitor cv = new MonitorThreadClassVisitor(Opcodes.ASM4, cw);
      cr.accept(cv, ClassReader.SKIP_DEBUG);

      byte[] transformedBytes = cw.toByteArray();
      return transformedBytes;
  }

}
