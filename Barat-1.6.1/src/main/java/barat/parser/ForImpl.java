/*
 * File: ForImpl.java
 *
 * $Id: ForImpl.java,v 1.10 2000/11/20 01:51:05 bokowski Exp $
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

/* generated by Boris Bokowski's rosegen on Sat Jul 11 18:34:54 GMT+03:30 1998
*/

import barat.reflect.For;
import barat.reflect.AExpression;

import barat.reflect.AStatement;
import barat.reflect.AForInit;
import barat.collections.AExpressionList;
import barat.collections.AExpressionArrayList;
import barat.collections.AExpressionIterator;
import java.util.*;
//import java.util.*;

public class ForImpl extends ALoopingStatementImpl implements For
{
	private AForInit forInit;
	private List<Attribute<AExpression>> updateExpressions = new ArrayList<Attribute<AExpression>>();
	public ForImpl( Attribute<AExpression> expression_, AStatement body_, AForInit forInit_, List<Attribute<AExpression>> updateExpressions_)
	{
		super( expression_, body_);
		forInit = forInit_;
		if(forInit!=null)
			forInit.setupContainment(this, "forInit");
		updateExpressions = updateExpressions_;
		for(Iterator<Attribute<AExpression>> i=updateExpressions.iterator(); i.hasNext();)
		{
			i.next().setupContainment(this, "updateExpressions");
		}
	}
	public ForImpl() {}
	public void setForInit( AForInit forInit_ )
	{
		forInit = forInit_;
		forInit.setupContainment(this, "forInit");
	}
	public AForInit getForInit()
	{
		return forInit;
	}
	public AExpressionList getUpdateExpressions()
	{
		AExpressionList result = new AExpressionArrayList();
		for(Iterator<Attribute<AExpression>> i = updateExpressions.iterator(); i.hasNext();)
		{
			result.add(i.next().value());
		}
		return result;
	}
	public void addUpdateExpression( Attribute<AExpression> updateExpression )
	{
		updateExpressions.add(updateExpression);
		updateExpression.setupContainment(this, "updateExpressions");
	}
	public void removeUpdateExpression( Attribute<AExpression> updateExpression )
	{
		updateExpressions.remove(updateExpression);
	}
	public void accept(barat.Visitor v)
	{
		v.visitFor(this);
	}
	public void accept(barat.parser.ImplementationVisitor v)
	{
		v.visitForImpl(this);
	}
}
