package com.xqq.asm.practise3;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.List;

/**
 * Created by rxiao on 2/27/15.
 */
public class AddFieldTransformer extends ClassTransformer{
    private int fieldAccess;
    private String fieldName;
    private String fieldDesc;

    public AddFieldTransformer(ClassTransformer ct, int fieldAccess, String fieldName, String fieldDesc) {
        super(ct);
        this.fieldAccess = fieldAccess;
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
    }

    @Override
    public void transform(ClassNode cn) {
        boolean isPresent = false;
        for (FieldNode fn: (List<FieldNode>) cn.fields) {
            if (fieldName.equals(fn.name)) {
                isPresent = true;
                break;
            }
        }
        if (!isPresent) {
            cn.fields.add(new FieldNode(fieldAccess, fieldName, fieldDesc, null, null));
            System.out.println("Field " + fieldName + " is added into Class:" + cn.name + ".");
        } else {
            System.out.println("Field " + fieldName + " already exists in Class:" + cn.name + ".");
        }
        super.transform(cn);
    }
}
