/*
 * File: InterfaceImpl.java
 *
 * $Id: InterfaceImpl.java,v 1.11 2000/11/20 01:51:06 bokowski Exp $
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

/* generated by Boris Bokowski's rosegen on Sat Jul 11 18:34:23 GMT+03:30 1998
*/

import barat.reflect.Interface;


import barat.reflect.Field;
import barat.collections.FieldList;
import barat.collections.FieldArrayList;
import barat.collections.FieldIterator;
import barat.reflect.AbstractMethod;
import barat.collections.AbstractMethodList;
import barat.collections.AbstractMethodArrayList;
import barat.collections.AbstractMethodIterator;
import barat.collections.InterfaceList;
import barat.collections.InterfaceArrayList;
import barat.collections.InterfaceIterator;
import barat.reflect.Class;

import barat.collections.BlockList;
import barat.collections.ClassList;
import barat.collections.ClassArrayList;
import barat.collections.ClassIterator;
import java.util.*;
//import java.util.*;

public class InterfaceImpl extends AUserTypeImpl implements Interface
{
	private List<Attribute<Interface>> extendedInterfaces = new ArrayList<Attribute<Interface>>();
	public InterfaceImpl( Modifiers delegate_modifiers_, String name_, FieldList fields_, AbstractMethodList abstractMethods_, List<Attribute<Interface>> nestedInterfaces_, List<Attribute<Class>> nestedClasses_, BlockList staticInitializers_)
	{
		super( delegate_modifiers_, name_, fields_, abstractMethods_, nestedInterfaces_, nestedClasses_, staticInitializers_);
	}
	public InterfaceImpl( Modifiers delegate_modifiers_, String name_, FieldList fields_, AbstractMethodList abstractMethods_, List<Attribute<Interface>> nestedInterfaces_, List<Attribute<Class>> nestedClasses_, Scope enclosingScope_, BlockList staticInitializers_)
	{
		super( delegate_modifiers_, name_, fields_, abstractMethods_, nestedInterfaces_, nestedClasses_, enclosingScope_, staticInitializers_);
	}
	public InterfaceImpl( Modifiers delegate_modifiers_, String name_, FieldList fields_, AbstractMethodList abstractMethods_, List<Attribute<Interface>> nestedInterfaces_, List<Attribute<Class>> nestedClasses_, Scope enclosingScope_, List<Attribute<Interface>> extendedInterfaces_, BlockList staticInitializers_)
	{
		super( delegate_modifiers_, name_, fields_, abstractMethods_, nestedInterfaces_, nestedClasses_, enclosingScope_, staticInitializers_);
		extendedInterfaces = extendedInterfaces_;
	}
	public InterfaceImpl() {}
	public InterfaceList getExtendedInterfaces()
	{
		InterfaceList result = new InterfaceArrayList();
		for(Iterator<Attribute<Interface>> i = extendedInterfaces.iterator(); i.hasNext();)
		{
			result.add(i.next().value());
		}
		return result;
	}
        public boolean isSubinterfaceOf (Interface superInterface) {
          return barat.parser.Typing.isSubinterfaceOf (this, superInterface);
        }
	public void addExtendedInterface( Attribute<Interface> extendedInterface )
	{
		extendedInterfaces.add(extendedInterface);
	}
	public void removeExtendedInterface( Attribute<Interface> extendedInterface )
	{
		extendedInterfaces.remove(extendedInterface);
	}
	public void accept(barat.Visitor v)
	{
		v.visitInterface(this);
	}
	public void accept(barat.parser.ImplementationVisitor v)
	{
		v.visitInterfaceImpl(this);
	}
}
