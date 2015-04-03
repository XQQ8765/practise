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

import com.xiaoqq.practise.threadmonitor.uuid.ThreadImplementationClassVisitor;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.*;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

public class Agent implements ClassFileTransformer {

  // Ignore list to eliminate endless recursion.
  private static String[] ignoreList = new String[]{
      "com/xiaoqq/practise/threadmonitor",
      "sun",
      "java",
      "com/intellij",
      "org/objectweb",
      "org/apache"
  };

  // A list of exceptions for the ignoreList list.
  private static String[] noignoreList = new String[]{};

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
    for (String anIgnore : ignoreList) {
      if (className.startsWith(anIgnore)) {
        for (String aNoignore : noignoreList) {
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
          //System.out.println("-------------------ignoreList. className:"+className + ", clazz:" +clazz.getName());
          return bytes;
      }
      //System.out.println("-------------------start to processs. className:"+className + ", clazz:" +clazz);
      ClassReader cr = new ClassReader(bytes);
      ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
      ClassVisitor cv = new ThreadImplementationClassVisitor(Opcodes.ASM5, cw);
      cr.accept(cv, ClassReader.EXPAND_FRAMES);

      byte[] transformedBytes = cw.toByteArray();

      //TODO: remove it
      try {
          FileUtils.writeByteArrayToFile(new File("d:\\workspace\\tmp\\generatedclasses\\" + className + "_gen.class"), transformedBytes);
      } catch (IOException e) {
          e.printStackTrace();
      }
      return transformedBytes;
  }

}
