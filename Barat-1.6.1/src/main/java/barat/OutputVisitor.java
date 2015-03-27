/*
 * File: OutputVisitor.java
 *
 * $Id: OutputVisitor.java,v 1.47 2003/07/24 13:02:21 bokowski Exp $
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

package barat;

import barat.reflect.*;
import barat.reflect.Class;
import barat.reflect.Package;
import barat.collections.*;
import barat.parser.NameAnalysis;

import java.io.*;
import java.util.StringTokenizer;

public class OutputVisitor implements Visitor
{
  protected PrintWriter out;
  protected boolean   isBinary;
  protected boolean printFullyQualifiedNames;
  static protected boolean printFullyQualifiedNamesDefault = false;

  public OutputVisitor(PrintWriter w, boolean fullyQualified) {
    out = w;
    printFullyQualifiedNames = fullyQualified;
  }

  public OutputVisitor()
  {
    this(printFullyQualifiedNamesDefault);
  }
  
  public OutputVisitor(boolean fullyQualified)
  {
    this(new PrintWriter(System.out, true), fullyQualified);
  }
  
  public OutputVisitor(Writer o)
  {
    this(o, printFullyQualifiedNamesDefault);
  }
  
  public OutputVisitor(Writer o, boolean fullyQualified)
  {
    this(new PrintWriter(o, true), fullyQualified);
  }
  
  public OutputVisitor(OutputStream o)
  {
    this(new PrintWriter(o, true), printFullyQualifiedNamesDefault);
  }
  
  public OutputVisitor(OutputStream o, boolean fullyQualified)
  {
    this(new PrintWriter(o, true), fullyQualified);
  }
  
  // helpers for printing

  protected void print (String s) {
    out.print (s);
  }

  protected void println (String s) {
    out.println (s);
  }

  protected void nl() {
    out.println ("");
  }

  protected int currentIndent = 0;
  
  protected void indent() {
    for (int i=0; i<currentIndent; i++)
      out.print ("  ");
  }
  
  protected void printTags(Node n)
  {
    StringList tags = n.getTags();

    if(tags != null)
    {
      print("/*:");
      for(StringIterator i = tags.iterator(); i.hasNext();)
      {
        print(i.next());
        if(i.hasNext()) print("*/ /*:");
      }
      print("*/ ");
    }
  }
  
  protected void printNewTags(Node n) {
    Tag[] tags = n.new_getTags();
    if(tags!=null) {
      nl(); indent();
      println("/**"); indent();
      print(" *");
      for(int i=0; i<tags.length; i++) {
        if(tags[i].getName()!=null && !tags[i].getName().equals("")) {
          print(" @" + tags[i].getName());
        }
        if(tags[i].getValue()!=null && !tags[i].getValue().equals("")) {
          StringTokenizer tokenizer = new StringTokenizer(tags[i].getValue(), "\n");
          while(tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken();
            println(" " + line); indent();
            print(" *");
          }
        } else {
          nl(); indent();
          print(" *");
        }
      }
      println("/"); indent();
    }
  }

  protected void printModifiers (AHasModifier o) {
    printNewTags(o);
    if (o.isPrivate())      print ("private ");
    if (o.isProtected())    print ("protected ");
    if (o.isPublic())       print ("public ");
    if (o.isAbstract())     print ("abstract ");
    if (o.isFinal())        print ("final ");
    if (o.isNative())       print ("native ");
    if (o.isStatic())       print ("static ");
    if (o.isSynchronized()) print ("synchronized ");
    if (o.isTransient())    print ("transient ");
    if (o.isVolatile())     print ("volatile ");
    if (o.isStrictfp())     print ("strictfp ");
    printTags(o);
  }

  // for printing a String literal

  protected String wrap(String s)
  {
    StringBuffer result = new StringBuffer();
    for(int i=0; i<s.length(); i++)
    {
	char c = s.charAt(i);
	switch(c)
	{
		case '\'':   result.append("'"); break;
		case '"':   result.append("\\\""); break;
		default:	result.append(wrap(c)); break;
	}
    }
    return result.toString();
  }

  protected String wrap(char c)
  {
      switch(c)
      {
        case '\\': return ("\\\\");
        case '\'' : return ("\\\'");
        case '\n' : return ("\\n");
        case '\r' : return ("\\r");
        case '\t' : return ("\\t");
        default  : return ""+c;
      }
  }
  
  protected void printTypeName(AType t, Node scope) {
    if(printFullyQualifiedNames) {
      print(NameAnalysis.qualifiedNameOf(t));
      return;
    }
    if(t instanceof AReferenceType) {
      if(t instanceof Array) {
        printTypeName(((Array)t).getElementType(), scope);
        print("[]");
      } else {
        if(t==scope.lookupUserType(t.getName())) {
          print(t.getName());
        } else {
          print(NameAnalysis.qualifiedNameOf(t));
        }
      }
    } else {
      print(t.getName());
    }
  }

  protected void printFieldName(Field f, Node scope) {
    if(printFullyQualifiedNames) {
      print(NameAnalysis.qualifiedNameOf(f));
      return;
    }
    if(f==scope.lookupVariable(f.getName())) {
      print(f.getName());
    } else {
      print(NameAnalysis.qualifiedNameOf(f));
    }
  }

  // visiting helpers

  protected void acceptIfPresent (barat.Node o) {
    if (o != null) o.accept ((Visitor)this);
  }

  // visiting methods below this line

  public void visitLiteral(Literal o)
  {
    java.lang.Object v = o.constantValue();
    if (v != null)
      if (v instanceof java.lang.String) 
        print ("\"" + wrap(v.toString()) + "\"");
      else if (v instanceof java.lang.Character)
        print ("'" + wrap(((Character)v).charValue()) + "'");
      else if (v instanceof java.lang.Float)
        print (v.toString() + "f");
      else if (v instanceof java.lang.Long)
        print (v.toString() + "L");
      else
        print (v.toString());
    else
      print ("null");
  }

  public void visitCaseBranch(CaseBranch o)
  {
    print ("case ");
    o.getConstantExpression().accept(this);
    println (":");  currentIndent++;
    for(AStatementIterator i=o.getStatements().iterator(); i.hasNext();)
    {
      indent();
      i.next().accept(this);
      nl();
    }
    currentIndent--;
  }

  public void visitInterface(Interface o)
  {
    isBinary = isBinaryClass(o);

    printModifiers (o);
    print ("interface ");
    print (o.getName() + " ");
    if (o.getExtendedInterfaces().size() > 0) {
      print ("extends ");
      for (InterfaceIterator i=o.getExtendedInterfaces().iterator();
           i.hasNext();) {
        printTypeName(i.next(), o.container());
        if (i.hasNext()) print (", "); else print (" ");
      }
    }
    println ("{");
    currentIndent++;

    for(FieldIterator i=o.getFields().iterator(); i.hasNext();)
    {
      indent();
      i.next().accept(this);
      nl();
    }

    for(AbstractMethodIterator i=o.getAbstractMethods().iterator(); i.hasNext();)
    {
      indent();
      i.next().accept(this);
      nl();
    }

    for(InterfaceIterator i=o.getNestedInterfaces().iterator(); i.hasNext();)
    {
      indent();
      i.next().accept(this);
      nl();
    }

    for(ClassIterator i=o.getNestedClasses().iterator(); i.hasNext();)
    {
      indent();
      i.next().accept(this);
      nl();
    }
    currentIndent--;
    indent();
    println ("}");
  }


  private static final boolean isBinaryClass(AUserType clazz) {
    return !((CompilationUnit)clazz.containing(CompilationUnit.class)).hasSource();
  }

  public void visitClass(Class o)
  {
    isBinary = isBinaryClass(o);

    if(!(o.container() instanceof AnonymousAllocation))
    {
      printModifiers (o); 
      print ("class ");
      print (o.getName());
      if (o.getSuperclass() != null) {
        String superClassName = o.getSuperclass().qualifiedName();
        if (!superClassName.equals ("java.lang.Object")) {
          print (" extends ");
          printTypeName(o.getSuperclass(), o.container());
        }
      }

      if (o.getImplementedInterfaces().size() > 0) {
        nl();
        currentIndent++;
        indent();
        print ("implements ");
        for (InterfaceIterator i=o.getImplementedInterfaces().iterator(); 
             i.hasNext();) {
          printTypeName(i.next(), o.container());
          if (i.hasNext()) print (", ");
        }
        currentIndent--;
      }
    }
    println (" {"); nl();
    currentIndent++;

    for (FieldIterator i=o.getFields().iterator(); 
         i.hasNext();)
    {
      indent();
      i.next().accept(this);
      nl(); nl();
    }
    for (BlockIterator i=o.getStaticInitializers().iterator(); 
         i.hasNext();)
    {
      indent();
      print("static ");i.next().accept(this);
      nl(); nl();
    }
    if(!(o.container() instanceof AnonymousAllocation))
    {
      for (ConstructorIterator i=o.getConstructors().iterator(); 
           i.hasNext();)
      {
        indent();
        i.next().accept(this);
        nl(); nl();
      }
    }
    for (AbstractMethodIterator i=o.getAbstractMethods().iterator(); 
         i.hasNext();)
    {
      indent();
      i.next().accept(this);
      nl(); nl();
    }
    for (ConcreteMethodIterator i=o.getConcreteMethods().iterator(); 
         i.hasNext();)
    {
      indent();
      i.next().accept(this);
      nl(); nl();
    }
    
    for(InterfaceIterator i=o.getNestedInterfaces().iterator(); i.hasNext();)
    {
      indent();
      i.next().accept(this);
      nl();
    }

    for(ClassIterator i=o.getNestedClasses().iterator(); i.hasNext();)
    {
      indent();
      i.next().accept(this);
      nl();
    }

    currentIndent--;
    indent();
    if(!(o.container() instanceof AnonymousAllocation))
    {
      println("}");
    }
    else
    {
      print("}");
    }
  }

  public void visitPackage(barat.reflect.Package o)
  {
    if(!o.getQualifiedName().toString().equals("")) {
      println("package " + o.getQualifiedName() + ";");
      nl();
    }
    for(InterfaceIterator i=o.getInterfaces().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(ClassIterator i=o.getClasses().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }

  public void visitCompilationUnit(CompilationUnit o)
  {
    Package p = (Package)o.container();
    if(!p.getQualifiedName().toString().equals("")) {
      println("package " + p.getQualifiedName() + ";");
    }
    if(!printFullyQualifiedNames) {
      Package[] packageImports = o.getPackageImports();
      for(int i=0; i<packageImports.length; i++) {
        if(i==0) nl();
        String packageName = packageImports[i].getQualifiedName().toString();
        println("import " + packageImports[i].getQualifiedName() + ".*;");
      }
      AUserType[] typeImports = o.getTypeImports();
      for(int i=0; i<typeImports.length; i++) {
        if(i==0) nl();
        println("import " + NameAnalysis.qualifiedNameOf(typeImports[i]) + ";");
      }
    }
    for(InterfaceIterator i=o.getInterfaces().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(ClassIterator i=o.getClasses().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }

  public void printMethodSignature(AMethod o)
  {
    printModifiers (o);
    if (o.getResultType() != null)
    {
      printTypeName(o.getResultType(), o.container());
    }
    else 
      print ("void");
    print (" " + o.getName() + "(");
    for(ParameterIterator i=o.getParameters().iterator(); i.hasNext();)
    {
      Parameter p = i.next();
      printTags(p);
	if(p.isFinal()) print("final ");
      printTypeName(p.getType(), o.container());
      print(" " + p.getName());
      if (i.hasNext()) print (", ");
    }
    print (")");
    if (o.getExceptions().size() > 0) {
      print (" throws ");
      for (ClassIterator i=o.getExceptions().iterator(); i.hasNext();) {
        printTypeName(i.next(), o.container());
        if (i.hasNext()) print (", ");
      }
    }
  }

  public void visitAbstractMethod(AbstractMethod o)
  {
    printMethodSignature(o);
    print (";");
  }

  public void visitParameter(Parameter o)
  {
    printTags(o);
	if(o.isFinal()) print("final ");
    printTypeName(o.getType(), o.container().container());
    print(" " + o.getName());
  }

  public void visitBlock(Block o)
  {
    printNewTags(o);
    if(o.label()!=null) print(o.label() + ": ");
    println ("{");
    currentIndent++;
    for(AStatementIterator i=o.getStatements().iterator(); i.hasNext();)
    {
      indent();
      i.next().accept(this);
      nl();
    }
    currentIndent--;
    indent();
    print ("}");
  }

  public void printConstructorSignature(Constructor o)
  {
    printModifiers (o);
    print (o.containingClass().getName() + "(");
    for (ParameterIterator i=o.getParameters().iterator(); i.hasNext();)
    {
      Parameter p = i.next();
      printTags(p);
	if(p.isFinal()) print("final ");
      printTypeName(p.getType(), o.container());
      print(" " + p.getName());
      if (i.hasNext()) print (", ");
    }
    print (")");
    if (o.getExceptions().size() > 0) {
      print (" throws ");
      for (ClassIterator i=o.getExceptions().iterator(); i.hasNext();) {
        printTypeName(i.next(), o.container());
        if (i.hasNext()) print (", ");
      }
    }
  }

  public void visitConstructor(Constructor o)
  {
    printConstructorSignature(o);
    println (" {");
    currentIndent++;

    indent();
    
    if(!isBinary)
    {
      o.getConstructorCall().accept(this);
      nl();

      // Don't visit the body.  Handle it here, because
      // this "body" must not start with an open brace.
      Block block = o.getBody();
      for (AStatementIterator i=block.getStatements().iterator(); 
           i.hasNext();) {
        indent();
        i.next().accept (this);
        nl();
      }
    }
    else
    {
      print("/* no source code available (class file) */");
      nl();
    }
    currentIndent--;
    indent();
    print ("}");
  }

  public void visitConcreteMethod(ConcreteMethod o)
  {
    printMethodSignature(o);
    if(!isBinary)
    {
      print(" ");
      o.getBody().accept(this);
    }
    else
    {
      println (" {");
      currentIndent++;
      indent();
      print(" /* no source code (class file) */");
      nl();
      currentIndent--;
      indent();
      print ("}");
    }
  }

  public void visitForInitDeclaration(ForInitDeclaration o)
  {
    boolean typeHasBeenOutput = false;
    for(VariableDeclarationIterator i=o.getDeclarations().iterator(); 
        i.hasNext();)
    {
      VariableDeclaration vd = i.next();
	LocalVariable lv = vd.getVariable();
	if(lv.isFinal()) print("final ");
	printTags(lv);
	if(!typeHasBeenOutput)
	{
	  typeHasBeenOutput = true;
	  printTypeName(lv.getType(), o.container());
          print(" ");
	}
      print (lv.getName());
    	if (lv.getInitializer() != null) {
      	print (" = ");
      	lv.getInitializer().accept (this);
    	}
      if (i.hasNext()) print (", ");
    }
  }

  public void visitVariableDeclaration(VariableDeclaration o)
  {
	if(o.getVariable().isFinal()) print("final ");
    o.getVariable().accept(this);
    if (!(o.container() instanceof ForInitDeclaration))
      print (";");
  }

  public void visitLocalVariable(LocalVariable o)
  {
    printTags(o);
    printNewTags(o);
    printTypeName(o.getType(), o.container());
    print(" " + o.getName());
    if (o.getInitializer() != null) {
      print (" = ");
      o.getInitializer().accept (this);
    }
  }

  public void visitField(Field o)
  {
    printModifiers (o);
    printTypeName(o.getType(), o.container());
    print(" " + o.getName());
    if (o.getInitializer() != null) {
      print (" = ");
      o.getInitializer().accept (this);
    }
    print (";");
  }

  public void visitThis(This o)
  {
//    if (o.getThisClass() != o.containingClass())
//      //*** Is this correct for both "this" and "super"?
//      //*** It is certainly wrong for inner classes of anonymous classes.
//      print (o.getThisClass().qualifiedName() + ".");

      if(!o.containingClass().isAssignableTo(o.getThisClass()))
      {
	  printTypeName(o.getThisClass(), o);
	  print(".");
      }
      if(o.isSuper())
      {
	  print ("super");
      }
      else
      {
	  print ("this");
      }
  }

  public void visitCatch(Catch o)
  {
    print (" catch (");
    o.getParameter().accept(this);
    print (") ");
    o.getBlock().accept(this);
  }

  public void visitFinally(Finally o)
  {
    print (" finally ");
    o.getBlock().accept(this);
  }

  public void visitAssert(Assert o)
  {
    printNewTags(o);
    print ("assert ");
    o.getConditionExpression().accept(this);
    if(o.getFailureExpression()!=null) {
      print (" : ");
      o.getFailureExpression().accept(this);
    }
    print(";");
  }

  public void visitSynchronized(Synchronized o)
  {
    printNewTags(o);
    if(o.label()!=null) print(o.label() + ": ");
    print ("synchronized (");
    o.getExpression().accept(this);
    print (") ");
    o.getBlock().accept(this);
  }

  public void visitConstructorCall(ConstructorCall o)
  {
    if (o.getCalledConstructor().containingClass() == o.containingClass())
      print ("this(");
    else
      print ("super(");
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this); if (i.hasNext()) print (", ");
    }
    print (");");
  }

  public void visitObjectAllocation(ObjectAllocation o)
  {
    if(o.getEnclosingInstance()!=null)
    {
      o.getEnclosingInstance().accept(this);
      print(".");
    }
    print ("new ");

    Node container = o.container();
    if(!((container instanceof AMethodCall) || (container instanceof ObjectAllocation) ||
       (container instanceof AnonymousAllocation)))
      printTags(o);

    if(o.getEnclosingInstance()!=null)
    {
      print(o.getCalledConstructor().containingClass().getName());
    }
    else
    {
      printTypeName(o.getCalledConstructor().containingClass(), o);
    }
    print("(");
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      AExpression expr = i.next();
      printTags(expr);
      expr.accept(this);
      if (i.hasNext()) print (", ");
    }
    print (")");
  }

  public void visitTry(Try o)
  {
    printNewTags(o);
    if(o.label()!=null) print(o.label() + ": ");
    print ("try ");
    o.getBlock().accept(this);
    for(CatchIterator i=o.getCatchClauses().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    acceptIfPresent (o.getFinallyClause());
  }

  public void visitArrayAccess(ArrayAccess o)
  {
    o.getArray().accept(this);
    print ("[");
    o.getIndex().accept(this);
    print ("]");
  }

  public void visitArrayAllocation(ArrayAllocation o)
  {
    print ("new ");
    printTags(o);
    printTypeName(o.getElementType(), o);
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      print ("[");
      i.next().accept(this);
      print ("]");
    }
    for (int i=0; i<o.freeDimensions(); i++) 
      print ("[]");
    acceptIfPresent (o.getInitializer());
  }

  public void visitArrayInitializer(ArrayInitializer o)
  {
    print ("{ ");
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
      if (i.hasNext()) print (", ");
    }
    print (" }");
  }

  public void visitAssignment(Assignment o)
  {
    o.getLvalue().accept(this);
    print (" " + o.operator() + " ");
    o.getOperand().accept(this);
  }

  public void visitBinaryOperation(BinaryOperation o)
  {
    o.getLeftOperand().accept(this);
    print (" " + o.operator() + " ");
    o.getRightOperand().accept(this);
  }

  public void visitCast(Cast o)
  {
    print ("(");
    printTypeName(o.getCastType(), o);
    print(")");
    o.getOperand().accept(this);
  }

  public void visitConditional(Conditional o)
  {
    o.getCondition().accept(this);
    print (" ? ");
    o.getIfTrue().accept(this);
    print (" : ");
    o.getIfFalse().accept(this);
  }

  public void visitContinue(Continue o)
  {
    printNewTags(o);
    print ("continue");
    if (o.getTarget() != null && o.getTarget().label()!=null) 
      print (" " + o.getTarget().label());
    print (";");
  }

  public void visitDefaultBranch(DefaultBranch o)
  {
    println ("default:");
    currentIndent++;
    for(AStatementIterator i=o.getStatements().iterator(); i.hasNext();)
    {
      indent();
      i.next().accept(this);
      nl();
    }
    currentIndent--;
  }

  public void visitDo(Do o)
  {
    printNewTags(o);
    if(o.label()!=null) print(o.label() + ": ");
    print ("do ");
    o.getBody().accept(this);
    print (" while (");
    o.getExpression().accept(this);
    print (");");
  }

  public void visitEmptyStatement(EmptyStatement o)
  {
    printNewTags(o);
    print (";");
  }

  public void visitExpressionStatement(ExpressionStatement o)
  {
    printNewTags(o);
    o.getExpression().accept(this);
    print (";");
  }

  public void visitFor(For o)
  {
    printNewTags(o);
    if(o.label()!=null) print(o.label() + ": ");
    print ("for (");
    acceptIfPresent (o.getForInit());
    print (";");
    acceptIfPresent (o.getExpression());
    print (";");
    for(AExpressionIterator i=o.getUpdateExpressions().iterator(); 
        i.hasNext();)
    {
      i.next().accept(this);
      if (i.hasNext()) print (", ");
    }
    print (") ");
    acceptIfPresent (o.getBody());
  }

  public void visitForInitExpression(ForInitExpression o)
  {
    for(AExpressionIterator i=o.getExpressions().iterator(); i.hasNext();)
    {
      i.next().accept(this); if (i.hasNext()) print (", ");
    }
  }

  public void visitIf(If o)
  {
    printNewTags(o);
    if(o.label()!=null) print(o.label() + ": ");
    print ("if (");
    o.getExpression().accept(this);
    print (") ");
    printTags(o);
    o.getThenBranch().accept(this);
    if (o.getElseBranch() != null) {
      print (" else ");
      o.getElseBranch().accept (this);
    }
  }

  public void visitInstanceFieldAccess(InstanceFieldAccess o)
  {
    o.getInstance().accept(this);
    print ("." + o.getField().getName());
  }

  public void visitInstanceMethodCall(InstanceMethodCall o)
  {
    printTags(o);
    o.getInstance().accept(this);
    print ("." + o.getCalledMethod().getName() + "(");
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      AExpression expr = i.next();
      printTags(expr);
      expr.accept(this);
      if (i.hasNext()) print (", ");
    }
    print (")");
  }

  public void visitArrayLengthAccess(ArrayLengthAccess o)
  {
    o.getOperand().accept(this);
    print (".length");
  }

  public void visitParenExpression(ParenExpression o)
  {
    print ("(");
    o.getOperand().accept(this);
    print (")");
  }

  public void visitReturn(Return o)
  {
    printNewTags(o);
    print ("return");
    printTags(o);
    if (o.getExpression() != null) {
      print (" ");
      o.getExpression().accept (this);
    }
    print (";");
  }

  public void visitStaticFieldAccess(StaticFieldAccess o)
  {
    printFieldName(o.getField(), o);
  }

  public void visitInstanceof(Instanceof o)
  {
    o.getOperand().accept(this);
    print(" instanceof ");
    printTypeName(o.getReferenceType(), o);
  }

  public void visitStaticMethodCall(StaticMethodCall o)
  {
    printTags(o);
    printTypeName(o.getCalledMethod().containingUserType(), o);
    print("." + o.getCalledMethod().getName() + "(");
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      AExpression expr = i.next();
      printTags(expr);
      expr.accept(this);
      if (i.hasNext()) print (", ");
    }
    print (")");
  }

  public void visitBreak(Break o)
  {
    printNewTags(o);
    print ("break");
    if (o.getTarget() != null)
      if(o.getTarget().label() != null)
		print (" " + o.getTarget().label());
    print (";");
  }

  public void visitSwitch(Switch o)
  {
    printNewTags(o);
    if(o.label()!=null) print(o.label() + ": ");
    print ("switch (");
    o.getExpression().accept(this);
    println (") {");
    for(ASwitchBranchIterator i=o.getBranches().iterator(); i.hasNext();) {
      indent();
      i.next().accept(this);
      nl();
    }
    indent(); println ("}");
  }

  public void visitUnaryOperation(UnaryOperation o)
  {
    if (o.isPostfix()) {
      o.getOperand().accept(this);
      print (o.operator());
    } else {
      print (o.operator());
      o.getOperand().accept(this);
    }
  }

  public void visitThrow(Throw o)
  {
    printNewTags(o);
    print ("throw ");
    o.getExpression().accept(this);
    print (";");
  }

  public void visitVariableAccess(VariableAccess o)
  {
    print(o.getVariable().getName());
  }

  public void visitWhile(While o)
  {
    printNewTags(o);
    if(o.label()!=null) print(o.label() + ": ");
    print ("while (");
    o.getExpression().accept(this);
    print (") ");
    o.getBody().accept(this);
  }

  public void visitAnonymousAllocation(AnonymousAllocation o)
  {
    print ("new ");
    Class c = o.getAnonymousClass();
    AUserType superType = null;
    if(c.getImplementedInterfaces().size()>0)
    {
      superType = c.getImplementedInterfaces().get(0);
    }
    else
    {
      superType = c.getSuperclass();
    }
    printTypeName(superType, o);
    print ("(");
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      AExpression expr = i.next();
      printTags(expr);
      expr.accept(this);
      if(i.hasNext()) print (", ");
    }
    print (") ");
    o.getAnonymousClass().accept(this);
    indent();
  }

  public void visitUserTypeDeclaration(UserTypeDeclaration o)
  {
    o.getUserType().accept(this);
  }

  public void visitClassExpression(ClassExpression o)
  {
    AType t = o.getType();
    if(t==null)
    {
      print("void");
    }
    else
    {
      printTypeName(t, o);
    }
    print(".class");
  }
        
  // hack warning: remove me!
  public final void visitArray(Array a) { }


  public static void main(String[] args) { // args[] are fully qualified class names 
    for(int i=0; i < args.length; i++) {
      AUserType t = barat.Barat.getUserType(args[i]); // Class or interface

      String pack  = t.qualifiedName();
      int    index = pack.lastIndexOf('.');
      if(index > 0) {
	pack = pack.substring(0, index);
	System.out.println("package " + pack + ";\n");
      }
      
      OutputVisitor out = new OutputVisitor();
      t.accept(out);
    }
  }
}
