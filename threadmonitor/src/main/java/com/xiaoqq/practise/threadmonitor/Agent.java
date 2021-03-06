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

import com.xiaoqq.practise.threadmonitor.relationship.LookupRelationShipClassVisitor;
import com.xiaoqq.practise.threadmonitor.uuid.MonitoringClassVisitor;
import com.xiaoqq.practise.threadmonitor.uuid.ThreadImplementationClassVisitor;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.util.CheckClassAdapter;

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
            "javax",
            "org",
            "com/sun",
            "com/intellij",
            "org/objectweb",
            "org/apache",
            "groovy"
    };

    // A list of exceptions for the ignoreList list.
    private static String[] noignoreList = new String[]{};

    public static void premain(String arg, Instrumentation instrumentation) {


        System.out.println("Start to create thread monitor Agent.");
        Agent agent = new Agent();
        instrumentation.addTransformer(agent, true);
        /*
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
        }   */
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

    public byte[] transform(ClassLoader classLoader, String className,
                            Class clazz, java.security.ProtectionDomain domain, byte[] bytes) {
        try {
            if (inIgnoreList(className)) {
                return bytes;
            }
            ClassReader cr = new ClassReader(bytes);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            /*ClassVisitor thCV = new ThreadImplementationClassVisitor(Opcodes.ASM5, cw, classLoader);
            ClassVisitor cv = new MonitoringClassVisitor(Opcodes.ASM5, thCV, classLoader);  */
            ClassVisitor cv = new LookupRelationShipClassVisitor(Opcodes.ASM5, cw, classLoader);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);

            byte[] transformedBytes = cw.toByteArray();

            //TODO: remove it
            try {
                FileUtils.writeByteArrayToFile(new File("d:\\workspace\\tmp\\generatedclasses\\" + className + "_gen.class"), transformedBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return transformedBytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
