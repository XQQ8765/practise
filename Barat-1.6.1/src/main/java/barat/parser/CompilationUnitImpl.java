/*
 * File: CompilationUnitImpl.java
 *
 * $Id: CompilationUnitImpl.java,v 1.18 2000/11/20 01:51:04 bokowski Exp $
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

/* generated by Boris Bokowski's rosegen on Sat Jul 11 18:34:25 GMT+03:30 1998
*/

import barat.reflect.CompilationUnit;

import barat.QualifiedName;
import barat.reflect.Interface;
import barat.reflect.Class;
import barat.reflect.AUserType;
import barat.reflect.Package;

import barat.collections.InterfaceList;
import barat.collections.InterfaceArrayList;
import barat.collections.InterfaceIterator;

import barat.collections.ClassList;
import barat.collections.ClassArrayList;
import barat.collections.ClassIterator;
import java.util.*;
//import java.util.*;

public class CompilationUnitImpl extends NodeImpl implements CompilationUnit, Scope
{
  private List<Attribute<Interface>> interfaces = new ArrayList<Attribute<Interface>>();
  private List<Attribute<Class>> classes = new ArrayList<Attribute<Class>>();
  private Scope enclosingScope;
  private Function2<String,Boolean,barat.reflect.ATyped> typedFinder = null;
  private Function2<String,Boolean,barat.reflect.AUserType> userTypeFinder = null;
  private String filename_ = null; 
  private boolean has_source = true;

  public boolean hasSource() { return has_source; }
  public void hasSource(boolean h) { has_source = h; }

        public void filename(String fn)
        {
          filename_ = fn;
        }
        public String filename()
        {
          return filename_;
        }
	public CompilationUnitImpl( List<Attribute<Interface>> interfaces_, List<Attribute<Class>> classes_)
	{
		super( );
		interfaces = interfaces_;
		for(Iterator<Attribute<Interface>> i=interfaces.iterator(); i.hasNext();)
		{
			i.next().setupContainment(this, "interfaces");
		}
		classes = classes_;
		for(Iterator<Attribute<Class>> i=classes.iterator(); i.hasNext();)
		{
			i.next().setupContainment(this, "classes");
		}
	}
	public CompilationUnitImpl( List<Attribute<Interface>> interfaces_, List<Attribute<Class>> classes_, Scope enclosingScope_)
	{
		super( );
		interfaces = interfaces_;
		for(Iterator<Attribute<Interface>> i=interfaces.iterator(); i.hasNext();)
		{
			i.next().setupContainment(this, "interfaces");
		}
		classes = classes_;
		for(Iterator<Attribute<Class>> i=classes.iterator(); i.hasNext();)
		{
			i.next().setupContainment(this, "classes");
		}
		enclosingScope = enclosingScope_;
	}
	public CompilationUnitImpl() {}
	public InterfaceList getInterfaces()
	{
		InterfaceList result = new InterfaceArrayList();
		for(Iterator<Attribute<Interface>> i = interfaces.iterator(); i.hasNext();)
		{
			result.add(i.next().value());
		}
		return result;
	}
	public void addInterface_( Attribute<Interface> interface_ )
	{
		interfaces.add(interface_);
		interface_.setupContainment(this, "interfaces");
	}
	public void removeInterface_( Attribute<Interface> interface_ )
	{
		interfaces.remove(interface_);
	}
	public ClassList getClasses()
	{
		ClassList result = new ClassArrayList();
		for(Iterator<Attribute<Class>> i = classes.iterator(); i.hasNext();)
		{
			result.add(i.next().value());
		}
		return result;
	}
	public void addClass_( Attribute<Class> class_ )
	{
		classes.add(class_);
		class_.setupContainment(this, "classes");
	}
	public void removeClass_( Attribute<Class> class_ )
	{
		classes.remove(class_);
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
        
        List<QualifiedName> typeImportsAsQualifiedNames;
        void setClassImports(List<QualifiedName> qualifiedNames) {
          typeImportsAsQualifiedNames = qualifiedNames;
        }
        
        public AUserType[] getTypeImports() {
          AUserType[] result = new AUserType[typeImportsAsQualifiedNames.size()];
          int i=0;
          for(Iterator<QualifiedName> it=typeImportsAsQualifiedNames.iterator();it.hasNext();) {
            result[i++] = NameAnalysis.lookupUserType(it.next());
          }
          return result;
        }
        
        List<Package> packageImports;
        void setPackageImports(List<Package> packages) {
          packageImports = packages;
        }
        
        public Package[] getPackageImports() {
          if(packageImports==null || packageImports.size()<2)
            return new Package[0];
          Package[] result = new Package[packageImports.size()-2];
          for(int i=1; i<packageImports.size()-1; i++) {
            result[i-1] = packageImports.get(i);
          }
          return result;
        }
        
	public void accept(barat.Visitor v)
	{
		v.visitCompilationUnit(this);
	}
	public void accept(barat.parser.ImplementationVisitor v)
	{
		v.visitCompilationUnitImpl(this);
	}
}
