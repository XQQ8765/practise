/*
 * File: AnalysisVisitor.java
 *
 * $Id: AnalysisVisitor.java,v 1.6 2002/10/28 10:38:18 bokowski Exp $
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

abstract public class AnalysisVisitor extends ImplementationVisitor
{
	public void visitLiteralImpl(LiteralImpl o)
	{}
	public void visitCaseBranchImpl(CaseBranchImpl o)
	{}
	public void visitInterfaceImpl(InterfaceImpl o)
	{}
	public void visitClassImpl(ClassImpl o)
	{}
	public void visitPackageImpl(PackageImpl o)
	{}
	public void visitCompilationUnitImpl(CompilationUnitImpl o)
	{}
	public void visitAbstractMethodImpl(AbstractMethodImpl o)
	{}
	public void visitParameterImpl(ParameterImpl o)
	{}
	public void visitBlockImpl(BlockImpl o)
	{}
	public void visitConstructorImpl(ConstructorImpl o)
	{}
	public void visitConcreteMethodImpl(ConcreteMethodImpl o)
	{}
	public void visitForInitDeclarationImpl(ForInitDeclarationImpl o)
	{}
	public void visitVariableDeclarationImpl(VariableDeclarationImpl o)
	{}
	public void visitLocalVariableImpl(LocalVariableImpl o)
	{}
	public void visitFieldImpl(FieldImpl o)
	{}
	public void visitThisImpl(ThisImpl o)
	{}
	public void visitCatchImpl(CatchImpl o)
	{}
	public void visitFinallyImpl(FinallyImpl o)
	{}
	public void visitSynchronizedImpl(SynchronizedImpl o)
	{}
	public void visitConstructorCallImpl(ConstructorCallImpl o)
	{}
	public void visitObjectAllocationImpl(ObjectAllocationImpl o)
	{}
	public void visitTryImpl(TryImpl o)
	{}
	public void visitArrayImpl(ArrayImpl o)
	{}
	public void visitArrayAccessImpl(ArrayAccessImpl o)
	{}
	public void visitArrayAllocationImpl(ArrayAllocationImpl o)
	{}
	public void visitArrayInitializerImpl(ArrayInitializerImpl o)
	{}
	public void visitAssertImpl(AssertImpl o)
	{}
	public void visitAssignmentImpl(AssignmentImpl o)
	{}
	public void visitBinaryOperationImpl(BinaryOperationImpl o)
	{}
	public void visitCastImpl(CastImpl o)
	{}
	public void visitConditionalImpl(ConditionalImpl o)
	{}
	public void visitContinueImpl(ContinueImpl o)
	{}
	public void visitDefaultBranchImpl(DefaultBranchImpl o)
	{}
	public void visitDoImpl(DoImpl o)
	{}
	public void visitEmptyStatementImpl(EmptyStatementImpl o)
	{}
	public void visitExpressionStatementImpl(ExpressionStatementImpl o)
	{}
	public void visitForImpl(ForImpl o)
	{}
	public void visitForInitExpressionImpl(ForInitExpressionImpl o)
	{}
	public void visitIfImpl(IfImpl o)
	{}
	public void visitInstanceFieldAccessImpl(InstanceFieldAccessImpl o)
	{}
	public void visitInstanceMethodCallImpl(InstanceMethodCallImpl o)
	{}
	public void visitArrayLengthAccessImpl(ArrayLengthAccessImpl o)
	{}
	public void visitParenExpressionImpl(ParenExpressionImpl o)
	{}
	public void visitReturnImpl(ReturnImpl o)
	{}
	public void visitStaticFieldAccessImpl(StaticFieldAccessImpl o)
	{}
	public void visitInstanceofImpl(InstanceofImpl o)
	{}
	public void visitStaticMethodCallImpl(StaticMethodCallImpl o)
	{}
	public void visitBreakImpl(BreakImpl o)
	{}
	public void visitSwitchImpl(SwitchImpl o)
	{}
	public void visitUnaryOperationImpl(UnaryOperationImpl o)
	{}
	public void visitThrowImpl(ThrowImpl o)
	{}
	public void visitVariableAccessImpl(VariableAccessImpl o)
	{}
	public void visitWhileImpl(WhileImpl o)
	{}
	public void visitAnonymousAllocationImpl(AnonymousAllocationImpl o)
	{}
	public void visitUserTypeDeclarationImpl(UserTypeDeclarationImpl o)
	{}
	public void visitClassExpressionImpl(ClassExpressionImpl o)
	{}
}
