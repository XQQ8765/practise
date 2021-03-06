/*
 * File: AMethodImpl.java
 *
 * $Id: AMethodImpl.java,v 1.17 2000/11/20 01:51:00 bokowski Exp $
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

import barat.reflect.AMethod;


import barat.reflect.Parameter;
import barat.collections.ParameterList;
import barat.collections.ParameterArrayList;
import barat.collections.ParameterIterator;
import barat.reflect.AType;

import barat.reflect.Class;

import barat.reflect.AUserType;
import barat.collections.ClassList;
import barat.collections.ClassArrayList;
import barat.collections.ClassIterator;
import java.util.*;
//import java.util.*;

public abstract class AMethodImpl extends NodeImpl implements AMethod, ImplAHasModifier, ImplANamed, Scope
{
	private Modifiers delegate_modifiers;
	private String name;
	private ParameterList parameters = new ParameterArrayList();
	private Attribute<AType> resultType;
	private List<Attribute<Class>> exceptions = new ArrayList<Attribute<Class>>();
	private Scope enclosingScope;
	private Function2<String,Boolean,barat.reflect.ATyped> typedFinder = null;
	private Function2<String,Boolean,barat.reflect.AUserType> userTypeFinder = null;
	public AMethodImpl( Modifiers delegate_modifiers_, String name_, ParameterList parameters_)
	{
		super( );
		delegate_modifiers = delegate_modifiers_;
		name = name_;
		parameters = parameters_;
		for(ParameterIterator i=parameters.iterator(); i.hasNext();)
		{
			i.next().setupContainment(this, "parameters");
		}
	}
	public AMethodImpl( Modifiers delegate_modifiers_, String name_, ParameterList parameters_, List<Attribute<Class>> exceptions_, Scope enclosingScope_)
	{
		super( );
		delegate_modifiers = delegate_modifiers_;
		name = name_;
		parameters = parameters_;
		for(ParameterIterator i=parameters.iterator(); i.hasNext();)
		{
			i.next().setupContainment(this, "parameters");
		}
		exceptions = exceptions_;
		enclosingScope = enclosingScope_;
	}
	public AMethodImpl( Modifiers delegate_modifiers_, String name_, ParameterList parameters_, Attribute<AType> resultType_, List<Attribute<Class>> exceptions_, Scope enclosingScope_)
	{
		super( );
		delegate_modifiers = delegate_modifiers_;
		name = name_;
		parameters = parameters_;
		for(ParameterIterator i=parameters.iterator(); i.hasNext();)
		{
			i.next().setupContainment(this, "parameters");
		}
		resultType = resultType_;
		exceptions = exceptions_;
		enclosingScope = enclosingScope_;
	}
	public AMethodImpl() {}
	public void setDelegate_modifiers( Modifiers delegate_modifiers_ )
	{
		delegate_modifiers = delegate_modifiers_;
	}
	public Modifiers getDelegate_modifiers()
	{
		return delegate_modifiers;
	}
		public boolean isAbstract()
	{
		return delegate_modifiers.isAbstract();
	}
		public void isAbstract(boolean isAbstract_)
	{
		delegate_modifiers.isAbstract(isAbstract_);
	}
		public boolean isFinal()
	{
		return delegate_modifiers.isFinal();
	}
		public void isFinal(boolean isFinal_)
	{
		delegate_modifiers.isFinal(isFinal_);
	}
		public boolean isNative()
	{
		return delegate_modifiers.isNative();
	}
		public void isNative(boolean isNative_)
	{
		delegate_modifiers.isNative(isNative_);
	}
		public boolean isPrivate()
	{
		return delegate_modifiers.isPrivate();
	}
		public void isPrivate(boolean isPrivate_)
	{
		delegate_modifiers.isPrivate(isPrivate_);
	}
		public boolean isProtected()
	{
		return delegate_modifiers.isProtected();
	}
		public void isProtected(boolean isProtected_)
	{
		delegate_modifiers.isProtected(isProtected_);
	}
		public boolean isPublic()
	{
		return delegate_modifiers.isPublic();
	}
		public void isPublic(boolean isPublic_)
	{
		delegate_modifiers.isPublic(isPublic_);
	}
		public boolean isStatic()
	{
		return delegate_modifiers.isStatic();
	}
		public void isStatic(boolean isStatic_)
	{
		delegate_modifiers.isStatic(isStatic_);
	}
		public boolean isSynchronized()
	{
		return delegate_modifiers.isSynchronized();
	}
		public void isSynchronized(boolean isSynchronized_)
	{
		delegate_modifiers.isSynchronized(isSynchronized_);
	}
		public boolean isTransient()
	{
		return delegate_modifiers.isTransient();
	}
		public void isTransient(boolean isTransient_)
	{
		delegate_modifiers.isTransient(isTransient_);
	}
		public boolean isVolatile()
	{
		return delegate_modifiers.isVolatile();
	}
		public void isVolatile(boolean isVolatile_)
	{
		delegate_modifiers.isVolatile(isVolatile_);
	}
		public boolean isStrictfp()
	{
		return delegate_modifiers.isStrictfp();
	}
		public void isStrictfp(boolean isStrictfp_)
	{
		delegate_modifiers.isStrictfp(isStrictfp_);
	}
	public void setName( String name_ )
	{
		name = name_;
	}
	public String getName()
	{
		return name;
	}
        public String qualifiedName() 
        {
                return barat.parser.NameAnalysis.qualifiedNameOf (this);
        }
	public ParameterList getParameters()
	{
		return parameters;
	}
	public void addParameter( Parameter parameter )
	{
		parameters.add(parameter);
		parameter.setupContainment(this, "parameters");
	}
	public void removeParameter( Parameter parameter )
	{
		parameters.remove(parameter);
	}
	public void setResultType( Attribute<AType> resultType_ )
	{
		resultType = resultType_;
//		resultType.setupContainment(this, "resultType");
	}
	public AType getResultType()
	{
		if(resultType!=null) return resultType.value(); else return null;
	}
	public ClassList getExceptions()
	{
		ClassList result = new ClassArrayList();
		for(Iterator<Attribute<Class>> i = exceptions.iterator(); i.hasNext();)
		{
			result.add(i.next().value());
		}
		return result;
	}
	public void addException( Attribute<Class> exception )
	{
		exceptions.add(exception);
	}
	public void removeException( Attribute<Class> exception )
	{
		exceptions.remove(exception);
	}
	public void setEnclosingScope( Scope enclosingScope_ )
	{
		enclosingScope = enclosingScope_;
	}
	public Scope getEnclosingScope()
	{
		return enclosingScope;
	}
	public Function2<String,Boolean,barat.reflect.ATyped> typedFinder()
	{
		return typedFinder;
	}
	public void typedFinder(Function2<String,Boolean,barat.reflect.ATyped> typedFinder_)
	{
		typedFinder = typedFinder_;
	}
	public Function2<String,Boolean,barat.reflect.AUserType> userTypeFinder()
	{
		return userTypeFinder;
	}
	public void userTypeFinder(Function2<String,Boolean,barat.reflect.AUserType> userTypeFinder_)
	{
		userTypeFinder = userTypeFinder_;
	}
        private AMethod overriddenMethod = null;
        public void setOverriddenMethod(AMethod m)
        {
	  if(m == this) return; // hack: this occurs because interfaces "implement" java.lang.Object
	  if(overriddenMethod==null) overriddenMethod = m; // hack: only the first call sets the variable
        }
        public AMethod getOverriddenMethod()
        {
	  if(overriddenMethod==null)
	  {
	    AUserTypeImpl ut = (AUserTypeImpl)containing(AUserTypeImpl.class);
	    ut.getMethodList(); // side-effect: sets overridden methods
	  }
	  return overriddenMethod;
	}
	abstract public void accept(barat.Visitor v);
	abstract public void accept(barat.parser.ImplementationVisitor v);
}
