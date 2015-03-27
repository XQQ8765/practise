/*
 * File: ImplementationVisitor.java
 *
 * $Id: ImplementationVisitor.java,v 1.7 2002/10/28 10:38:19 bokowski Exp $
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


abstract public class ImplementationVisitor
{
	abstract public void visitLiteralImpl(LiteralImpl o);
	abstract public void visitCaseBranchImpl(CaseBranchImpl o);
	abstract public void visitInterfaceImpl(InterfaceImpl o);
	abstract public void visitClassImpl(ClassImpl o);
	abstract public void visitPackageImpl(PackageImpl o);
	abstract public void visitCompilationUnitImpl(CompilationUnitImpl o);
	abstract public void visitAbstractMethodImpl(AbstractMethodImpl o);
	abstract public void visitParameterImpl(ParameterImpl o);
	abstract public void visitBlockImpl(BlockImpl o);
	abstract public void visitConstructorImpl(ConstructorImpl o);
	abstract public void visitConcreteMethodImpl(ConcreteMethodImpl o);
	abstract public void visitForInitDeclarationImpl(ForInitDeclarationImpl o);
	abstract public void visitVariableDeclarationImpl(VariableDeclarationImpl o);
	abstract public void visitLocalVariableImpl(LocalVariableImpl o);
	abstract public void visitFieldImpl(FieldImpl o);
	abstract public void visitThisImpl(ThisImpl o);
	abstract public void visitCatchImpl(CatchImpl o);
	abstract public void visitFinallyImpl(FinallyImpl o);
	abstract public void visitSynchronizedImpl(SynchronizedImpl o);
	abstract public void visitConstructorCallImpl(ConstructorCallImpl o);
	abstract public void visitObjectAllocationImpl(ObjectAllocationImpl o);
	abstract public void visitTryImpl(TryImpl o);
	abstract public void visitArrayImpl(ArrayImpl o);
	abstract public void visitArrayAccessImpl(ArrayAccessImpl o);
	abstract public void visitArrayAllocationImpl(ArrayAllocationImpl o);
	abstract public void visitArrayInitializerImpl(ArrayInitializerImpl o);
	abstract public void visitAssertImpl(AssertImpl o);
	abstract public void visitAssignmentImpl(AssignmentImpl o);
	abstract public void visitBinaryOperationImpl(BinaryOperationImpl o);
	abstract public void visitCastImpl(CastImpl o);
	abstract public void visitConditionalImpl(ConditionalImpl o);
	abstract public void visitContinueImpl(ContinueImpl o);
	abstract public void visitDefaultBranchImpl(DefaultBranchImpl o);
	abstract public void visitDoImpl(DoImpl o);
	abstract public void visitEmptyStatementImpl(EmptyStatementImpl o);
	abstract public void visitExpressionStatementImpl(ExpressionStatementImpl o);
	abstract public void visitForImpl(ForImpl o);
	abstract public void visitForInitExpressionImpl(ForInitExpressionImpl o);
	abstract public void visitIfImpl(IfImpl o);
	abstract public void visitInstanceFieldAccessImpl(InstanceFieldAccessImpl o);
	abstract public void visitInstanceMethodCallImpl(InstanceMethodCallImpl o);
	abstract public void visitArrayLengthAccessImpl(ArrayLengthAccessImpl o);
	abstract public void visitParenExpressionImpl(ParenExpressionImpl o);
	abstract public void visitReturnImpl(ReturnImpl o);
	abstract public void visitStaticFieldAccessImpl(StaticFieldAccessImpl o);
	abstract public void visitInstanceofImpl(InstanceofImpl o);
	abstract public void visitStaticMethodCallImpl(StaticMethodCallImpl o);
	abstract public void visitBreakImpl(BreakImpl o);
	abstract public void visitSwitchImpl(SwitchImpl o);
	abstract public void visitUnaryOperationImpl(UnaryOperationImpl o);
	abstract public void visitThrowImpl(ThrowImpl o);
	abstract public void visitVariableAccessImpl(VariableAccessImpl o);
	abstract public void visitWhileImpl(WhileImpl o);
	abstract public void visitAnonymousAllocationImpl(AnonymousAllocationImpl o);
	abstract public void visitUserTypeDeclarationImpl(UserTypeDeclarationImpl o);
	abstract public void visitClassExpressionImpl(ClassExpressionImpl o);
}
