/*
 * File: DescendingVisitor.java
 *
 * $Id: DescendingVisitor.java,v 1.15 2002/10/28 10:38:16 bokowski Exp $
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

import barat.collections.*;

/**
 * Descends into a Barat structure, staying inside the current
 * class or interface.  Should be subclassed in order to do something
 * useful.
 */
public class DescendingVisitor implements Visitor
{
  protected void acceptIfPresent (barat.Node o) {
    if (o != null) o.accept (this);
  }

  // visiting methods below this line

  public void visitLiteral(Literal o)
  {
  }
  public void visitCaseBranch(CaseBranch o)
  {
    o.getConstantExpression().accept(this);
    for(AStatementIterator i=o.getStatements().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitInterface(Interface o)
  {
    for(FieldIterator i=o.getFields().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(AbstractMethodIterator i=o.getAbstractMethods().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(InterfaceIterator i=o.getNestedInterfaces().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(ClassIterator i=o.getNestedClasses().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitClass(Class o)
  {
    for(BlockIterator i=o.getStaticInitializers().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(ConstructorIterator i=o.getConstructors().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(FieldIterator i=o.getFields().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(AbstractMethodIterator i=o.getAbstractMethods().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(ConcreteMethodIterator i=o.getConcreteMethods().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(InterfaceIterator i=o.getNestedInterfaces().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(ClassIterator i=o.getNestedClasses().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitPackage(barat.reflect.Package o)
  {
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
    for(InterfaceIterator i=o.getInterfaces().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    for(ClassIterator i=o.getClasses().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitAbstractMethod(AbstractMethod o)
  {
    for(ParameterIterator i=o.getParameters().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitParameter(Parameter o)
  {
  }
  public void visitBlock(Block o)
  {
    for(AStatementIterator i=o.getStatements().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitConstructor(Constructor o)
  {
    for(ParameterIterator i=o.getParameters().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    acceptIfPresent (o.getConstructorCall());
    o.getBody().accept(this);
  }
  public void visitConcreteMethod(ConcreteMethod o)
  {
    for(ParameterIterator i=o.getParameters().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.getBody().accept(this);
  }
  public void visitForInitDeclaration(ForInitDeclaration o)
  {
    for(VariableDeclarationIterator i=o.getDeclarations().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitVariableDeclaration(VariableDeclaration o)
  {
    o.getVariable().accept(this);
  }
  public void visitLocalVariable(LocalVariable o)
  {
    acceptIfPresent (o.getInitializer());
  }
  public void visitField(Field o)
  {
    acceptIfPresent (o.getInitializer());
  }
  public void visitThis(This o)
  {
  }
  public void visitCatch(Catch o)
  {
    o.getParameter().accept(this);
    o.getBlock().accept(this);
  }
  public void visitFinally(Finally o)
  {
    o.getBlock().accept(this);
  }
  public void visitSynchronized(Synchronized o)
  {
    o.getExpression().accept(this);
    o.getBlock().accept(this);
  }
  public void visitConstructorCall(ConstructorCall o)
  {
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitObjectAllocation(ObjectAllocation o)
  {
    acceptIfPresent (o.getEnclosingInstance());

    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitTry(Try o)
  {
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
    o.getIndex().accept(this);
  }
  public void visitArrayAllocation(ArrayAllocation o)
  {
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    acceptIfPresent (o.getInitializer());
  }
  public void visitArrayInitializer(ArrayInitializer o)
  {
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitAssert(Assert o)
  {
    o.getConditionExpression().accept(this);
    if(o.getFailureExpression()!=null) o.getFailureExpression().accept(this);
  }
  public void visitAssignment(Assignment o)
  {
    o.getLvalue().accept(this);
    o.getOperand().accept(this);
  }
  public void visitBinaryOperation(BinaryOperation o)
  {
    o.getLeftOperand().accept(this);
    o.getRightOperand().accept(this);
  }
  public void visitCast(Cast o)
  {
    o.getOperand().accept(this);
  }
  public void visitConditional(Conditional o)
  {
    o.getCondition().accept(this);
    o.getIfTrue().accept(this);
    o.getIfFalse().accept(this);
  }
  public void visitContinue(Continue o)
  {
    /*** o.getTarget().accept(this); ***/
  }
  public void visitDefaultBranch(DefaultBranch o)
  {
    for(AStatementIterator i=o.getStatements().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitDo(Do o)
  {
    o.getBody().accept(this);
    o.getExpression().accept(this);
  }
  public void visitEmptyStatement(EmptyStatement o)
  {
  }
  public void visitExpressionStatement(ExpressionStatement o)
  {
    o.getExpression().accept(this);
  }
  public void visitFor(For o)
  {
    acceptIfPresent (o.getForInit());
    acceptIfPresent (o.getExpression());
    for(AExpressionIterator i=o.getUpdateExpressions().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    acceptIfPresent (o.getBody());
  }
  public void visitForInitExpression(ForInitExpression o)
  {
    for(AExpressionIterator i=o.getExpressions().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitIf(If o)
  {
    o.getExpression().accept(this);
    o.getThenBranch().accept(this);
    acceptIfPresent (o.getElseBranch());
  }
  public void visitInstanceFieldAccess(InstanceFieldAccess o)
  {
    o.getInstance().accept(this);
  }
  public void visitInstanceMethodCall(InstanceMethodCall o)
  {
    o.getInstance().accept(this);
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitArrayLengthAccess(ArrayLengthAccess o)
  {
    o.getOperand().accept(this);
  }
  public void visitParenExpression(ParenExpression o)
  {
    o.getOperand().accept(this);
  }
  public void visitReturn(Return o)
  {
    acceptIfPresent (o.getExpression());
  }
  public void visitStaticFieldAccess(StaticFieldAccess o)
  {
  }
  public void visitInstanceof(Instanceof o)
  {
    o.getOperand().accept(this);
  }
  public void visitStaticMethodCall(StaticMethodCall o)
  {
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitBreak(Break o)
  {
    /*** o.getTarget().accept(this); ***/
  }
  public void visitSwitch(Switch o)
  {
    o.getExpression().accept(this);
    for(ASwitchBranchIterator i=o.getBranches().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
  }
  public void visitUnaryOperation(UnaryOperation o)
  {
    o.getOperand().accept(this);
  }
  public void visitThrow(Throw o)
  {
    o.getExpression().accept(this);
  }
  public void visitVariableAccess(VariableAccess o)
  {
  }
  public void visitWhile(While o)
  {
    o.getExpression().accept(this);
    o.getBody().accept(this);
  }
  public void visitAnonymousAllocation(AnonymousAllocation o)
  {
    for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();)
    {
      i.next().accept(this);
    }
    o.getAnonymousClass().accept(this);
  }
  public void visitUserTypeDeclaration(UserTypeDeclaration o)
  {
    o.getUserType().accept(this);
  }
  public void visitClassExpression(ClassExpression o)
  {
  }
        
  // hack warning: remove me!
  public final void visitArray(Array a) { }
}
