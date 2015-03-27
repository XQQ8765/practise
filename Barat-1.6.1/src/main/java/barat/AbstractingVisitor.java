/*
 * File: AbstractingVisitor.java
 *
 * $Id: AbstractingVisitor.java,v 1.7 2002/10/28 10:38:16 bokowski Exp $
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

public class AbstractingVisitor implements Visitor
{
	public void visitNode(Node o) { }
	public void visitAExpression(AExpression o) { visitNode(o); }
        public void visitALValue(ALValue o) { visitAExpression(o); }
        public void visitAFieldAccess(AFieldAccess o) { visitALValue(o); }
        public void visitAOperandExpression(AOperandExpression o) { visitAExpression(o); }
        public void visitAArgumentsExpression(AArgumentsExpression o) { visitAExpression(o); }
        public void visitAMethodCall(AMethodCall o) { visitAArgumentsExpression(o); }
        public void visitAHasModifier(AHasModifier o) { }
        public void visitANamed(ANamed o) { }
        public void visitATyped(ATyped o) { }
        public void visitAMethod(AMethod o) { visitAHasModifier(o); visitANamed(o); }
        public void visitAUserType(AUserType o) { visitAHasModifier(o); visitANamed(o); }
        public void visitAVariable(AVariable o) { visitANamed(o); visitATyped(o); }
        public void visitAStatement(AStatement o) { }
        public void visitATargetStatement(ATargetStatement o) { visitAStatement(o); }
        public void visitALoopingStatement(ALoopingStatement o) { visitATargetStatement(o); }
        public void visitAForInit(AForInit o) { }
        public void visitASwitchBranch(ASwitchBranch o) { }

	public void visitLiteral(Literal o) { visitAExpression(o); }
	public void visitCaseBranch(CaseBranch o) { visitASwitchBranch(o); }
	public void visitInterface(Interface o) { visitAUserType(o); }
	public void visitClass(Class o) { visitAUserType(o); }
	public void visitPackage(barat.reflect.Package o) { }
	public void visitCompilationUnit(CompilationUnit o) { }
	public void visitAbstractMethod(AbstractMethod o) { visitAMethod(o); }
	public void visitParameter(Parameter o) { visitAVariable(o); }
	public void visitBlock(Block o) { visitATargetStatement(o); }
	public void visitConstructor(Constructor o) { visitAMethod(o); }
	public void visitConcreteMethod(ConcreteMethod o) { visitAMethod(o); }
	public void visitForInitDeclaration(ForInitDeclaration o) { visitAForInit(o); }
	public void visitVariableDeclaration(VariableDeclaration o) { visitAStatement(o); }
	public void visitLocalVariable(LocalVariable o) { visitAVariable(o); }
	public void visitField(Field o) { visitAHasModifier(o); visitANamed(o); visitATyped(o); }
	public void visitThis(This o) { visitAExpression(o); }
	public void visitCatch(Catch o) { }
	public void visitFinally(Finally o) { }
	public void visitAssert(Assert o) { visitAStatement(o); }
	public void visitSynchronized(Synchronized o) { visitATargetStatement(o); }
	public void visitConstructorCall(ConstructorCall o) { }
	public void visitObjectAllocation(ObjectAllocation o) { visitAArgumentsExpression(o); }
	public void visitTry(Try o) { visitATargetStatement(o); }
	public void visitArrayAccess(ArrayAccess o) { visitALValue(o); }
	public void visitArrayAllocation(ArrayAllocation o) { visitAArgumentsExpression(o); }
	public void visitArrayInitializer(ArrayInitializer o) { visitAArgumentsExpression(o); }
	public void visitAssignment(Assignment o) { visitAOperandExpression(o); }
	public void visitBinaryOperation(BinaryOperation o) { visitAExpression(o); }
	public void visitCast(Cast o) { visitAOperandExpression(o); }
	public void visitConditional(Conditional o) { visitAExpression(o); }
	public void visitContinue(Continue o) { visitAStatement(o); }
	public void visitDefaultBranch(DefaultBranch o) { visitASwitchBranch(o); }
	public void visitDo(Do o) { visitALoopingStatement(o); }
	public void visitEmptyStatement(EmptyStatement o) { visitAStatement(o); }
	public void visitExpressionStatement(ExpressionStatement o) { visitAStatement(o); }
	public void visitFor(For o) { visitALoopingStatement(o); }
	public void visitForInitExpression(ForInitExpression o) { visitAForInit(o); }
	public void visitIf(If o) { visitATargetStatement(o); }
	public void visitInstanceFieldAccess(InstanceFieldAccess o) { visitAFieldAccess(o); }
	public void visitInstanceMethodCall(InstanceMethodCall o) { visitAMethodCall(o); }
	public void visitArrayLengthAccess(ArrayLengthAccess o) { visitAOperandExpression(o); }
	public void visitParenExpression(ParenExpression o) { visitAOperandExpression(o); }
	public void visitReturn(Return o) { visitAStatement(o); }
	public void visitStaticFieldAccess(StaticFieldAccess o) { visitAFieldAccess(o); }
	public void visitInstanceof(Instanceof o) { visitAOperandExpression(o); }
	public void visitStaticMethodCall(StaticMethodCall o) { visitAMethodCall(o); }
	public void visitBreak(Break o) { visitAStatement(o); }
	public void visitSwitch(Switch o) { visitATargetStatement(o); }
	public void visitUnaryOperation(UnaryOperation o) { visitAOperandExpression(o); }
	public void visitThrow(Throw o) { visitAStatement(o); }
	public void visitVariableAccess(VariableAccess o) { visitALValue(o); }
	public void visitWhile(While o) { visitALoopingStatement(o); }
	public void visitAnonymousAllocation(AnonymousAllocation o) { visitObjectAllocation(o); }
	public void visitUserTypeDeclaration(UserTypeDeclaration o) { visitAStatement(o); }
	public void visitClassExpression(ClassExpression o) { visitAExpression(o); }
        
        // hack warning: remove me!
        public final void visitArray(Array a) { }
}
