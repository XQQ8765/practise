package com.xqq.asm.mergetwoclasses;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;

/**
 * Created by rxiao on 3/12/15.
 */
public class MergeClassUtil {

    /**
     * Merge the two classes.
     * @param c1 Class
     * @param c2 Class
     * @return byte[]
     * @throws IOException
     */
    public static byte[] mergeClass(Class c1, Class c2) throws IOException {
        MergeClassNode mcn = new MergeClassNode();

        ClassReader classReader = new ClassReader(c1.getName());
        classReader.accept(mcn, ClassReader.SKIP_DEBUG);

        classReader = new ClassReader(c2.getName());
        classReader.accept(mcn, ClassReader.SKIP_DEBUG);

        ClassWriter cw = new ClassWriter(0);
        mcn.accept(cw);
        return cw.toByteArray();
    }
}
