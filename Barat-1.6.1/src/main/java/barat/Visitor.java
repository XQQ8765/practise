/*
 * File: Visitor.java
 *
 * $Id: Visitor.java,v 1.18 2002/10/28 10:38:17 bokowski Exp $
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

/**
 * General Visitor Interface for Barat Structures
 */
public interface Visitor
{
	public void visitLiteral(Literal o);
	public void visitCaseBranch(CaseBranch o);
	public void visitInterface(Interface o);
	public void visitClass(Class o);
	public void visitPackage(barat.reflect.Package o);
	public void visitCompilationUnit(CompilationUnit o);
	public void visitAbstractMethod(AbstractMethod o);
	public void visitParameter(Parameter o);
	public void visitBlock(Block o);
	public void visitConstructor(Constructor o);
	public void visitConcreteMethod(ConcreteMethod o);
	public void visitForInitDeclaration(ForInitDeclaration o);
	public void visitVariableDeclaration(VariableDeclaration o);
	public void visitLocalVariable(LocalVariable o);
	public void visitField(Field o);
	public void visitThis(This o);
	public void visitCatch(Catch o);
	public void visitFinally(Finally o);
	public void visitSynchronized(Synchronized o);
	public void visitConstructorCall(ConstructorCall o);
	public void visitObjectAllocation(ObjectAllocation o);
	public void visitTry(Try o);
	public void visitArrayAccess(ArrayAccess o);
	public void visitArrayAllocation(ArrayAllocation o);
	public void visitArrayInitializer(ArrayInitializer o);
	public void visitAssignment(Assignment o);
	public void visitBinaryOperation(BinaryOperation o);
	public void visitCast(Cast o);
	public void visitConditional(Conditional o);
	public void visitContinue(Continue o);
	public void visitDefaultBranch(DefaultBranch o);
	public void visitDo(Do o);
	public void visitEmptyStatement(EmptyStatement o);
	public void visitExpressionStatement(ExpressionStatement o);
	public void visitFor(For o);
	public void visitForInitExpression(ForInitExpression o);
	public void visitIf(If o);
	public void visitInstanceFieldAccess(InstanceFieldAccess o);
	public void visitInstanceMethodCall(InstanceMethodCall o);
	public void visitArrayLengthAccess(ArrayLengthAccess o);
	public void visitParenExpression(ParenExpression o);
	public void visitAssert(Assert o);
	public void visitReturn(Return o);
	public void visitStaticFieldAccess(StaticFieldAccess o);
	public void visitInstanceof(Instanceof o);
	public void visitStaticMethodCall(StaticMethodCall o);
	public void visitBreak(Break o);
	public void visitSwitch(Switch o);
	public void visitUnaryOperation(UnaryOperation o);
	public void visitThrow(Throw o);
	public void visitVariableAccess(VariableAccess o);
	public void visitWhile(While o);
	public void visitAnonymousAllocation(AnonymousAllocation o);
	public void visitUserTypeDeclaration(UserTypeDeclaration o);
        public void visitClassExpression(ClassExpression o);

        // *** HACK WARNING.  Remove me!
        public void visitArray (Array o);
}
