/*
 * File: AttributeAdderVisitor.java
 *
 * $Id: AttributeAdderVisitor.java,v 1.2 2000/11/20 01:51:02 bokowski Exp $
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

import barat.Visitor;
import barat.Node;

final public class AttributeAdderVisitor extends AnalysisVisitor
{
	private Visitor visitor;
	public AttributeAdderVisitor(Visitor v)
	{
		visitor = v;
	}
	private void delegate(Node n)
	{
		n.accept(visitor);
	}
	public void visitLiteralImpl(LiteralImpl o)
	{ delegate(o); }
	public void visitCaseBranchImpl(CaseBranchImpl o)
	{ delegate(o); }
	public void visitInterfaceImpl(InterfaceImpl o)
	{ delegate(o); }
	public void visitClassImpl(ClassImpl o)
	{ delegate(o); }
	public void visitPackageImpl(PackageImpl o)
	{ delegate(o); }
	public void visitCompilationUnitImpl(CompilationUnitImpl o)
	{ delegate(o); }
	public void visitAbstractMethodImpl(AbstractMethodImpl o)
	{ delegate(o); }
	public void visitParameterImpl(ParameterImpl o)
	{ delegate(o); }
	public void visitBlockImpl(BlockImpl o)
	{ delegate(o); }
	public void visitConstructorImpl(ConstructorImpl o)
	{ delegate(o); }
	public void visitConcreteMethodImpl(ConcreteMethodImpl o)
	{ delegate(o); }
	public void visitForInitDeclarationImpl(ForInitDeclarationImpl o)
	{ delegate(o); }
	public void visitVariableDeclarationImpl(VariableDeclarationImpl o)
	{ delegate(o); }
	public void visitLocalVariableImpl(LocalVariableImpl o)
	{ delegate(o); }
	public void visitFieldImpl(FieldImpl o)
	{ delegate(o); }
	public void visitThisImpl(ThisImpl o)
	{ delegate(o); }
	public void visitCatchImpl(CatchImpl o)
	{ delegate(o); }
	public void visitFinallyImpl(FinallyImpl o)
	{ delegate(o); }
	public void visitSynchronizedImpl(SynchronizedImpl o)
	{ delegate(o); }
	public void visitConstructorCallImpl(ConstructorCallImpl o)
	{ delegate(o); }
	public void visitObjectAllocationImpl(ObjectAllocationImpl o)
	{ delegate(o); }
	public void visitTryImpl(TryImpl o)
	{ delegate(o); }
	public void visitArrayImpl(ArrayImpl o)
	{ delegate(o); }
	public void visitArrayAccessImpl(ArrayAccessImpl o)
	{ delegate(o); }
	public void visitArrayAllocationImpl(ArrayAllocationImpl o)
	{ delegate(o); }
	public void visitArrayInitializerImpl(ArrayInitializerImpl o)
	{ delegate(o); }
	public void visitAssignmentImpl(AssignmentImpl o)
	{ delegate(o); }
	public void visitBinaryOperationImpl(BinaryOperationImpl o)
	{ delegate(o); }
	public void visitCastImpl(CastImpl o)
	{ delegate(o); }
	public void visitConditionalImpl(ConditionalImpl o)
	{ delegate(o); }
	public void visitContinueImpl(ContinueImpl o)
	{ delegate(o); }
	public void visitDefaultBranchImpl(DefaultBranchImpl o)
	{ delegate(o); }
	public void visitDoImpl(DoImpl o)
	{ delegate(o); }
	public void visitEmptyStatementImpl(EmptyStatementImpl o)
	{ delegate(o); }
	public void visitExpressionStatementImpl(ExpressionStatementImpl o)
	{ delegate(o); }
	public void visitForImpl(ForImpl o)
	{ delegate(o); }
	public void visitForInitExpressionImpl(ForInitExpressionImpl o)
	{ delegate(o); }
	public void visitIfImpl(IfImpl o)
	{ delegate(o); }
	public void visitInstanceFieldAccessImpl(InstanceFieldAccessImpl o)
	{ delegate(o); }
	public void visitInstanceMethodCallImpl(InstanceMethodCallImpl o)
	{ delegate(o); }
	public void visitArrayLengthAccessImpl(ArrayLengthAccessImpl o)
	{ delegate(o); }
	public void visitParenExpressionImpl(ParenExpressionImpl o)
	{ delegate(o); }
	public void visitReturnImpl(ReturnImpl o)
	{ delegate(o); }
	public void visitStaticFieldAccessImpl(StaticFieldAccessImpl o)
	{ delegate(o); }
	public void visitInstanceofImpl(InstanceofImpl o)
	{ delegate(o); }
	public void visitStaticMethodCallImpl(StaticMethodCallImpl o)
	{ delegate(o); }
	public void visitBreakImpl(BreakImpl o)
	{ delegate(o); }
	public void visitSwitchImpl(SwitchImpl o)
	{ delegate(o); }
	public void visitUnaryOperationImpl(UnaryOperationImpl o)
	{ delegate(o); }
	public void visitThrowImpl(ThrowImpl o)
	{ delegate(o); }
	public void visitVariableAccessImpl(VariableAccessImpl o)
	{ delegate(o); }
	public void visitWhileImpl(WhileImpl o)
	{ delegate(o); }
	public void visitAnonymousAllocationImpl(AnonymousAllocationImpl o)
	{ delegate(o); }
	public void visitUserTypeDeclarationImpl(UserTypeDeclarationImpl o)
	{ delegate(o); }
	public void visitClassExpressionImpl(ClassExpressionImpl o)
	{ delegate(o); }
}
