/*
 * File: AArgumentsExpressionImpl.java
 *
 * $Id: AArgumentsExpressionImpl.java,v 1.10 2000/11/20 01:51:00 bokowski Exp $
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

/* generated by Boris Bokowski's rosegen on Sat Jul 11 18:35:32 GMT+03:30 1998
*/

import barat.reflect.AArgumentsExpression;
import barat.reflect.AExpression;

import barat.collections.AExpressionList;
import barat.collections.AExpressionArrayList;
import barat.collections.AExpressionIterator;
import java.util.*;
//import java.util.*;

public abstract class AArgumentsExpressionImpl extends AExpressionImpl implements AArgumentsExpression
{
	private List<Attribute<AExpression>> arguments = new ArrayList<Attribute<AExpression>>();
	public AArgumentsExpressionImpl( List<Attribute<AExpression>> arguments_)
	{
		super( );
		arguments = arguments_;
		for(Iterator<Attribute<AExpression>> i=arguments.iterator(); i.hasNext();)
		{
			i.next().setupContainment(this, "arguments");
		}
	}
	public AArgumentsExpressionImpl() {}
	public AExpressionList getArguments()
	{
		AExpressionList result = new AExpressionArrayList();
		for(Iterator<Attribute<AExpression>> i = arguments.iterator(); i.hasNext();)
		{
			result.add(i.next().value());
		}
		return result;
	}
	public void addArgument( Attribute<AExpression> argument )
	{
		arguments.add(argument);
		argument.setupContainment(this, "arguments");
	}
	public void removeArgument( Attribute<AExpression> argument )
	{
		arguments.remove(argument);
	}
	abstract public void accept(barat.Visitor v);
	abstract public void accept(barat.parser.ImplementationVisitor v);
}
