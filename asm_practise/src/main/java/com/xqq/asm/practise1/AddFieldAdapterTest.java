package com.xqq.asm.practise1;

import org.objectweb.asm.ClassReader;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;

/**
 * Created by rxiao on 2/2/15.
 */
public class AddFieldAdapterTest {

    public static void main(String[] args) throws IOException {
        addField("java.io.CharArrayReader");
        addField("java.io.FileInputStream");
        addField("java.lang.Comparable");
        addField("com.xqq.asm.practise1.AddFieldAdapterTest");
    }

    private static void addField(String className) throws IOException {
        ClassReader classReader = new ClassReader(className);
        ClassWriter classWriter = new ClassWriter(classReader, 0);
        AddFieldAdapter addFieldAdapter = new AddFieldAdapter(
                classWriter,
                ACC_PUBLIC + ACC_FINAL,
                "pos",
                "I");
        classReader.accept(addFieldAdapter, 0);
    }
}
