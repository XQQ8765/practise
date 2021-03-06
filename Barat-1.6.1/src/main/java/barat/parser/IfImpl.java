/*
 * File: IfImpl.java
 *
 * $Id: IfImpl.java,v 1.8 2000/11/20 01:51:06 bokowski Exp $
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

/* generated by Boris Bokowski's rosegen on Sat Jul 11 18:34:55 GMT+03:30 1998
*/

import barat.reflect.If;
import barat.reflect.AExpression;

import barat.reflect.AStatement;

public class IfImpl extends ATargetStatementImpl implements If
{
	private Attribute<AExpression> expression;
	private AStatement thenBranch;
	private AStatement elseBranch;
	public IfImpl( Attribute<AExpression> expression_, AStatement thenBranch_, AStatement elseBranch_)
	{
		super( );
		expression = expression_;
		if(expression!=null)
			expression.setupContainment(this, "expression");
		thenBranch = thenBranch_;
		if(thenBranch!=null)
			thenBranch.setupContainment(this, "thenBranch");
		elseBranch = elseBranch_;
		if(elseBranch!=null)
			elseBranch.setupContainment(this, "elseBranch");
	}
	public IfImpl() {}
	public void setExpression( Attribute<AExpression> expression_ )
	{
		expression = expression_;
		expression.setupContainment(this, "expression");
	}
	public AExpression getExpression()
	{
		return expression.value();
	}
	public void setThenBranch( AStatement thenBranch_ )
	{
		thenBranch = thenBranch_;
		thenBranch.setupContainment(this, "thenBranch");
	}
	public AStatement getThenBranch()
	{
		return thenBranch;
	}
	public void setElseBranch( AStatement elseBranch_ )
	{
		elseBranch = elseBranch_;
		elseBranch.setupContainment(this, "elseBranch");
	}
	public AStatement getElseBranch()
	{
		return elseBranch;
	}
	public void accept(barat.Visitor v)
	{
		v.visitIf(this);
	}
	public void accept(barat.parser.ImplementationVisitor v)
	{
		v.visitIfImpl(this);
	}
}
