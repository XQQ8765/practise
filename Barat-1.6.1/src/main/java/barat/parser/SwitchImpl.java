/*
 * File: SwitchImpl.java
 *
 * $Id: SwitchImpl.java,v 1.7 2000/11/20 01:51:09 bokowski Exp $
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

/* generated by Boris Bokowski's rosegen on Sat Jul 11 18:35:25 GMT+03:30 1998
*/

import barat.reflect.Switch;
import barat.reflect.AExpression;

import barat.reflect.ASwitchBranch;
import barat.collections.ASwitchBranchList;
import barat.collections.ASwitchBranchArrayList;
import barat.collections.ASwitchBranchIterator;

public class SwitchImpl extends ATargetStatementImpl implements Switch
{
	private Attribute<AExpression> expression;
	private ASwitchBranchList branches = new ASwitchBranchArrayList();
	public SwitchImpl( Attribute<AExpression> expression_, ASwitchBranchList branches_)
	{
		super( );
		expression = expression_;
		if(expression!=null)
			expression.setupContainment(this, "expression");
		branches = branches_;
		for(ASwitchBranchIterator i=branches.iterator(); i.hasNext();)
		{
			i.next().setupContainment(this, "branches");
		}
	}
	public SwitchImpl() {}
	public void setExpression( Attribute<AExpression> expression_ )
	{
		expression = expression_;
		expression.setupContainment(this, "expression");
	}
	public AExpression getExpression()
	{
		return expression.value();
	}
	public ASwitchBranchList getBranches()
	{
		return branches;
	}
	public void addBranche( ASwitchBranch branche )
	{
		branches.add(branche);
		branche.setupContainment(this, "branches");
	}
	public void removeBranche( ASwitchBranch branche )
	{
		branches.remove(branche);
	}
	public void accept(barat.Visitor v)
	{
		v.visitSwitch(this);
	}
	public void accept(barat.parser.ImplementationVisitor v)
	{
		v.visitSwitchImpl(this);
	}
}
