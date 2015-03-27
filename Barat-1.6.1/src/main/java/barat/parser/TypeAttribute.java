/*
 * File: TypeAttribute.java
 *
 * $Id: TypeAttribute.java,v 1.2 2000/11/20 01:51:10 bokowski Exp $
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

import barat.reflect.AType;

abstract class TypeAttribute<A> extends Attribute<A>
{
	String typeName;
	public TypeAttribute(String typeName_)
	{
		typeName = typeName_;
	}
	/** -1: not equal, 0: can't determine without parsing (or already parsed), 1: equal */
	int quickEqualsType(TypeAttribute<A> other)
	{
		if(typeName==null || other.typeName==null) return 0;
		String tn1 = typeName;
		String tn2 = other.typeName;
		if(hasValue()) tn1 = ((AType)value()).qualifiedName();
		if(other.hasValue()) tn2 = ((AType)other.value()).qualifiedName();
		// make sure that typeNames are fully qualified:
		if(!isFullyQualified(tn1)) return 0;
		if(!isFullyQualified(tn2)) return 0;
		
		return tn1.equals(tn2)?1:-1;
	}
	private boolean isFullyQualified(String s)
	{
		if(s.indexOf('.')!=-1) return true;
		if(s.equals("int")) return true;
		if(s.equals("boolean")) return true;
		if(s.equals("float")) return true;
		if(s.equals("char")) return true;
		if(s.equals("double")) return true;
		if(s.equals("long")) return true;
		if(s.equals("byte")) return true;
		if(s.equals("short")) return true;
		return false;
	}
}
