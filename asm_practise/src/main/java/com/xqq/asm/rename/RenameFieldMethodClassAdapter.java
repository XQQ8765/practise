package com.xqq.asm.rename;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;

/**
 * Created by rxiao on 3/12/15.
 */
public class RenameFieldMethodClassAdapter extends RemappingClassAdapter {

    public RenameFieldMethodClassAdapter(ClassVisitor cv) {
        super(cv, new NameRemapper());
    }

    private static class NameRemapper extends Remapper {
        @Override
        public String mapFieldName(String owner, String name, String desc) {
            return super.mapFieldName(owner, RenameMapUtil.getReplacedFieldName(name), desc);
        }

        @Override
        public String mapMethodName(String owner, String name, String desc) {
            return super.mapMethodName(owner, RenameMapUtil.getReplacedMethodName(name), desc);
        }
    }
}
