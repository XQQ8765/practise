/*
 * File: AssertImpl.java
 *
 * $Id: AssertImpl.java,v 1.1 2002/10/28 10:38:18 bokowski Exp $
 *
 * This file is part of Barat.
 * Copyright (c) 1998-2002 Boris Bokowski (bokowski@users.sourceforge.net)
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

import barat.reflect.Assert;
import barat.reflect.AExpression;

public class AssertImpl extends AStatementImpl implements Assert
{
	private Attribute<AExpression> conditionExpression;
	private Attribute<AExpression> failureExpression;
	public AssertImpl( Attribute<AExpression> conditionExpression_, Attribute<AExpression> failureExpression_)
	{
		super( );
		conditionExpression = conditionExpression_;
		if(conditionExpression!=null)
			conditionExpression.setupContainment(this, "conditionExpression");
		failureExpression = failureExpression_;
		if(failureExpression!=null)
			failureExpression.setupContainment(this, "failureExpression");
	}
	public AssertImpl() {}
	public void setConditionExpression( Attribute<AExpression> expression_ )
	{
		conditionExpression = expression_;
		conditionExpression.setupContainment(this, "conditionExpression");
	}
	public AExpression getConditionExpression()
	{
		return conditionExpression.value();
	}
	public void setFailureExpression( Attribute<AExpression> expression_ )
	{
		failureExpression = expression_;
		failureExpression.setupContainment(this, "failureExpression");
	}
	public AExpression getFailureExpression()
	{
		return failureExpression==null?null:failureExpression.value();
	}
	public void accept(barat.Visitor v)
	{
		v.visitAssert(this);
	}
	public void accept(barat.parser.ImplementationVisitor v)
	{
		v.visitAssertImpl(this);
	}
}
