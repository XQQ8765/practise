/*
 * File: ClassFileParser.java
 *
 * $Id: ClassFileParser.java,v 1.23 2003/07/24 10:58:49 bokowski Exp $
 *
 * This file is part of Barat.
 * Copyright (c) 1998-2000 Boris Bokowski (bokowski@users.sourceforge.net)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 *   Neither the name of Boris Bokowski nor the names of his contributors
 *   may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BORIS BOKOWSKI
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package barat.parser;

import barat.reflect.*;
import barat.reflect.Class;
import barat.*;

import java.io.*;
import org.apache.bcel.classfile.*;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.Constants;
import java.util.Vector;
import java.util.*;
//import java.util.*;

class ClassFileParser implements org.apache.bcel.Constants
{
  private static Factory factory() { 
    return Factory.getInstance();
  }

  static AUserTypeImpl parseClassFile(InputStream is, String fileName)
  {
    ClassParser cp = new ClassParser(is, fileName);
    JavaClass jc;
    try
    {
      jc = cp.parse();
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
      throw new RuntimeException("IO error");
    }
    String className = jc.getClassName();
    String enclosingName = null;
    AUserTypeImpl enclosingUserType = null;
    QualifiedName packageName = QualifiedName.from(className);
    if(packageName.isQualified())
    {
      className = packageName.getBaseName();
      if (className.indexOf('$') != -1) {
		enclosingName = className.substring(0, className.indexOf('$'));
		className = className.substring(className.indexOf('$')+1);
		if (Character.isDigit(className.charAt(0)))
		  return null; // no need to load inner classes of the form ClassName$1
		//System.out.println("Inner class: " + enclosingName + '.' + className);
      }
      packageName = packageName.getQualifier();
    }
    else
    {
      packageName = QualifiedName.from("");
    }
    PackageImpl pkg = factory().getPackage(packageName.toString());
    CompilationUnitImpl compilationUnit = factory().createCompilationUnit(jc.getFileName(), new ArrayList<QualifiedName>(), new ArrayList<barat.reflect.Package>());
    compilationUnit.hasSource(false);
    compilationUnit.setEnclosingScope (pkg);
    compilationUnit.setupContainment(pkg, "compilationUnit");
    ClassImpl newClass = null;
    InterfaceImpl newInterface = null;
    AUserTypeImpl ut = null;
    boolean isClass = jc.isClass();
    NodeImpl container = compilationUnit;

    if (enclosingName != null) {
      enclosingUserType = NameAnalysis.lookupUserType(pkg, enclosingName);
      container = enclosingUserType;
      if (enclosingUserType.getNestedUserType(className) != null)
		return null;
    }

    if(isClass)
    {
      newClass = factory().createClass(className, container);
      ut = newClass;
      newClass.setSuperclass(
        NameAnalysis.createClassAttribute(
          QualifiedName.from(jc.getSuperclassName()),
          null));
    }
    else
    {
      newInterface = factory().createInterface(className, container);
      ut = newInterface;
    }

    if (enclosingUserType != null)
      ut.setEnclosingScope(enclosingUserType);
    else
      ut.setEnclosingScope(pkg);
    setupModifiers(jc.getAccessFlags(), ut);

    org.apache.bcel.classfile.Attribute[] attributes;
    if((attributes = stripAttributes(jc.getAttributes())) != null)
      ut.addAttribute(AttributeWrapper.class, new AttributeWrapper(attributes));

    String[] interfaceNames = jc.getInterfaceNames();
    for(int i=0; i<interfaceNames.length; i++)
    {
      if(isClass)
      {
        newClass.addImplementedInterface(
          NameAnalysis.createInterfaceAttribute(
            QualifiedName.from(interfaceNames[i]),
            null));
      }
      else
      {
        newInterface.addExtendedInterface(
          NameAnalysis.createInterfaceAttribute(
            QualifiedName.from(interfaceNames[i]),
            null));
      }
    }
    Field[] fields = jc.getFields();
    for(int i=0; i<fields.length; i++)
    {
      FieldImpl newField = factory().createField();
      newField.setName(fields[i].getName());
      newField.setType(
        TypeAnalysis.createTypeAttributeFromSignature(
          fields[i].getSignature()));
      setupModifiers(fields[i].getAccessFlags(), newField);
      ConstantValue cv = fields[i].getConstantValue();
      if(cv!=null)
      {
         Constant c = jc.getConstantPool().getConstant(cv.getConstantValueIndex());

          Object   val;
          int    idx;

          // Print constant to string depending on its type
          switch(c.getTag()) {
          case Constants.CONSTANT_Long:    val = new Long(((ConstantLong)c).getBytes());    break;
          case Constants.CONSTANT_Float:   val = new Float(((ConstantFloat)c).getBytes());   break;
          case Constants.CONSTANT_Double:  val = new Double(((ConstantDouble)c).getBytes());  break;
          case Constants.CONSTANT_Integer: val = new Integer(((ConstantInteger)c).getBytes()); break;
          case Constants.CONSTANT_String:  
            idx   = ((ConstantString)c).getStringIndex();
            c   = jc.getConstantPool().getConstant(idx, Constants.CONSTANT_Utf8);
            val = ((ConstantUtf8)c).getBytes();
            break;
          default: throw new RuntimeException("Type of ConstValue invalid: " + c);
          }
        newField.setInitializer(new barat.parser.Constant<AExpression>(factory().createLiteral(val)));
      }

      ut.addField(newField);

      if((attributes = stripAttributes(fields[i].getAttributes())) != null)
	newField.addAttribute(AttributeWrapper.class, new AttributeWrapper(attributes));
    }

    Method[] methods = jc.getMethods();
    for(int i=0; i<methods.length; i++)
    {
      String mname = methods[i].getName();
      AMethodImpl newMethod;

      if(mname.equals("<init>"))
      {
        ConstructorImpl newConstructor = factory().createConstructor();
        newConstructor.setBody(factory().createBlock());
        newClass.addConstructor(newConstructor);
        newMethod = newConstructor;
      }
      else if(mname.equals("<clinit>"))
      {
        BlockImpl newBlock = factory().createBlock();
        ut.addStaticInitializer(newBlock);
	continue;
      }
      else if(methods[i].isAbstract())
      {
        AbstractMethodImpl newAbstractMethod = factory().createAbstractMethod();
        ut.addAbstractMethod(newAbstractMethod);
        newMethod = newAbstractMethod;
      }
      else
      {
        ConcreteMethodImpl newConcreteMethod = factory().createConcreteMethod();
        newConcreteMethod.setBody(factory().createBlock());
        newClass.addConcreteMethod(newConcreteMethod);
        newMethod = newConcreteMethod;
      }
      
      addExceptions(methods[i], newMethod);

      setupModifiers(methods[i].getAccessFlags(), newMethod);
      newMethod.setName(mname);
      newMethod.setEnclosingScope(ut);
      String sig = methods[i].getSignature();
      addParametersFromSignature(sig, newMethod);
      String resultSig = sig.substring(sig.indexOf(')')+1);
      newMethod.setResultType(TypeAnalysis.createTypeAttributeFromSignature(resultSig));

      if((attributes = stripAttributes(methods[i].getAttributes())) != null)
	newMethod.addAttribute(AttributeWrapper.class, new AttributeWrapper(attributes));
    }
    return ut;
  }

  private static org.apache.bcel.classfile.Attribute[] stripAttributes(org.apache.bcel.classfile.Attribute[] attributes) {
    if(attributes != null) {
      Vector vec = new Vector();

      for(int i=0; i < attributes.length; i++) {
	switch(attributes[i].getTag()) { // Add only interesting attributes
	case ATTR_SOURCE_FILE: case ATTR_CONSTANT_VALUE: case ATTR_CODE:
	case ATTR_EXCEPTIONS: case ATTR_LINE_NUMBER_TABLE: case ATTR_LOCAL_VARIABLE_TABLE:
	  break;

	default:
	  vec.addElement(attributes[i]);
	}
      }

      if(vec.size() > 0) {
	attributes = new org.apache.bcel.classfile.Attribute[vec.size()];
	vec.copyInto(attributes);
	return attributes;
      }
    }

    return null;
  }

  private static void addExceptions(Method method, AMethodImpl new_method) {
    ExceptionTable table = method.getExceptionTable();
    if(table != null) {
      String[] list = table.getExceptionNames();

      for(int i=0; i < list.length; i++)
	new_method.addException(NameAnalysis.createClassAttribute(QualifiedName.from(list[i]), null));
    }
  }

  private static void setupModifiers(int flags, ImplAHasModifier o)
  {
    o.setDelegate_modifiers(new Modifiers());
 		o.isAbstract((flags & Constants.ACC_ABSTRACT) != 0);
 		o.isPublic((flags & Constants.ACC_PUBLIC) != 0);
 		o.isPrivate((flags & Constants.ACC_PRIVATE) != 0);
 		o.isProtected((flags & Constants.ACC_PROTECTED) != 0);
 		o.isFinal((flags & Constants.ACC_FINAL) != 0);
 		o.isNative((flags & Constants.ACC_NATIVE) != 0);
		if(!(o instanceof AUserTypeImpl))
		  o.isSynchronized((flags & Constants.ACC_SYNCHRONIZED) != 0);
 		o.isStatic((flags & Constants.ACC_STATIC) != 0);
 		o.isVolatile((flags & Constants.ACC_VOLATILE) != 0);
 		o.isTransient((flags & Constants.ACC_TRANSIENT) != 0);
  }
  private static void addParametersFromSignature(String sig, AMethodImpl method)
  {
  	if(sig.charAt(0)!='(')
  	{
  		throw new RuntimeException("invalid signature");
  	}
  	int parameter_no = 0;
  	int start = 1;
	char[] chs = sig.toCharArray();

  	while(chs[start] != ')')
  	{
  		int stop = start;
  		loop: while(true)
  		{
  			char ch = chs[stop];
  			switch (ch)
  			{
  				case '[':
  					while(ch == '[')
  					{
  						stop++;
  						ch = chs[stop];
  					}
  					continue loop;
  				case 'L':
  					stop = sig.indexOf(';', stop);
  					break loop;
  				default:
  					break loop;
  			}
  		}
  		String ftype = sig.substring(start, stop+1);
  		ParameterImpl newParameter = factory().createParameter(
  		  TypeAnalysis.createTypeAttributeFromSignature(ftype),
  		  "p" + (parameter_no++));
  		method.addParameter(newParameter);
  		start = stop+1;
  	}
  }
}
