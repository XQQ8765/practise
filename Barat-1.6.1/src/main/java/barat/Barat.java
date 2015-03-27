/*
 * File: Barat.java
 *
 * $Id: Barat.java,v 1.24 2000/11/20 01:50:50 bokowski Exp $
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

import barat.parser.*;
import barat.reflect.Class;
import barat.reflect.Interface;
import barat.reflect.AUserType;
import barat.reflect.AMethod;
import barat.reflect.ConcreteMethod;
import java.util.Hashtable;
import java.util.Vector;
import barat.collections.*;

/**
 * The Barat top level API.
 */
public class Barat {

  private static String debugLoading_ = System.getProperty("barat.debugLoading");
  public static boolean debugLoading = debugLoading_==null ? false : !debugLoading_.equals("false");
  private static String preferByteCode_ = System.getProperty("barat.preferByteCode");
  public static boolean preferByteCode = preferByteCode_==null ? false : !preferByteCode_.equals("false");

  // class path sanity check
  private static boolean classPathUsed = false;

  public static void setClassPath (String path) {
    if (classPathUsed)
      throw new RuntimeException 
        ("class path already in use, should not be re-adjusted");
    else
      try {
        NameAnalysis.classPath = new ClassPath (path);
      } catch (java.io.IOException e) {
        e.printStackTrace();
      }
  }

  public static barat.reflect.AUserType getUserType (String name) {
    classPathUsed = true;
    return NameAnalysis.lookupUserType (Factory.getInstance().getPackage (""),
                                        QualifiedName.from (name));
  }

  public static barat.reflect.Class getClass (String name) {
    classPathUsed = true;
    barat.reflect.AUserType t =
      NameAnalysis.lookupUserType (Factory.getInstance().getPackage (""),
                                   QualifiedName.from (name));

    if (t instanceof barat.reflect.Class)
      return (barat.reflect.Class)t;
    else if (t instanceof barat.reflect.Interface)
      throw new RuntimeException (name + " is an interface, not a class");
    else
      throw new RuntimeException (name + ": Huh?");
  }

  public static barat.reflect.Interface getInterface (String name) {
    classPathUsed = true;
    barat.reflect.AUserType t =
      NameAnalysis.lookupUserType (Factory.getInstance().getPackage (""),
                                   QualifiedName.from (name));
      
    if (t instanceof barat.reflect.Interface)
      return (barat.reflect.Interface)t;
    else if (t instanceof barat.reflect.Class)
      throw new RuntimeException (name + " is a class, not an interface");
    else
      throw new RuntimeException (name + ": Huh?");
  }
  
  // Use this method with care: package.getClasses() loads and parses all
  // classes and interfaces of that package!
  public static barat.reflect.Package getPackage(String name) {
    return Factory.getInstance().getPackage(name);
  }

  public static barat.reflect.Package[] getImmediateSubPackages(barat.reflect.Package p) {
    String packageName = p.getQualifiedName().toString();

    // this is ugly, but it causes the class path to be set:
    getObjectClass();

    ClassPath cp = NameAnalysis.classPath;

    String[] subPackageNames = cp.subPackages(packageName);

    barat.reflect.Package[] result = new barat.reflect.Package[subPackageNames.length];
    for(int i=0; i<subPackageNames.length; i++) {
      result[i] = getPackage(subPackageNames[i]);
    }

    return result;
  }
  
  private static void addAllSubPackages(barat.reflect.Package p, Vector result) {
    barat.reflect.Package[] immediateSubPackages = getImmediateSubPackages(p);
    for(int i=0; i<immediateSubPackages.length; i++) {
      result.addElement(immediateSubPackages[i]);
      addAllSubPackages(immediateSubPackages[i], result);
    }
  }

  public static barat.reflect.Package[] getAllSubPackages(barat.reflect.Package p) {
    Vector result_ = new Vector();
    addAllSubPackages(p, result_);
    barat.reflect.Package[] result = new barat.reflect.Package[result_.size()];
    result_.copyInto(result);
    return result;
  }
  
  public static barat.reflect.Class getObjectClass()
  {
    return barat.parser.NameAnalysis.getObjectClass();
  }

  public static barat.reflect.Class getStringClass()
  {
    return barat.parser.NameAnalysis.getStringClass();
  }

  public static barat.reflect.Interface getThrowableInterface()
  {
    return barat.parser.NameAnalysis.getThrowableInterface();
  }

  public static barat.reflect.Interface getCloneableInterface()
  {
    return barat.parser.NameAnalysis.getCloneableInterface();
  }
  
  /** This method registers a Visitor which is called for every
   *  AST node created by Barat. This can be used for adding lazily
   *  evaluated attributes to certain types of AST nodes, but not for
   *  much else because the visitor is called while the created node is
   *  not yet fully initialized.
   */
  public static void registerAttributeAdder(Visitor v)
  {
  	barat.parser.Factory.registerAttributeAdder(v);
  }

  /**
   * Parse a compilation unit that is contained in a file.
   * This is a lower-level method than, e.g., getClass() or getInterface().
   * Please consider using those methods rather than this one.
   */
  public static barat.reflect.CompilationUnit parseCompilationUnit(java.io.File f)
  {
    return barat.parser.Factory.parseCompilationUnit(f);
  }

  /**
   * Main method for testing.
   */
  public static void main (String args[]) {
    if (args.length >= 1)
    {
	    if(args[0].equals("-p"))
	    {
        	    for(int i=1; i<args.length; i++) {
        	      try
        	      {
                	barat.reflect.Package p = Barat.getPackage(args[i]);
                	for(barat.collections.ClassIterator it = p.getClasses().iterator();
                	    it.hasNext();)
                	{
				OutputVisitor v = new OutputVisitor();
				it.next().accept (v);
                	}
                	for(barat.collections.InterfaceIterator it = p.getInterfaces().iterator();
                	    it.hasNext();)
                	{
				OutputVisitor v = new OutputVisitor();
				it.next().accept (v);
                	}
        	      }
        	      catch(Exception ex)
        	      {
                	ex.printStackTrace();
                	return;
        	      }
        	    }
	    }
	    else
	    {
	      for(int i=0; i<args.length; i++)
	      {
        	barat.reflect.AUserType t = getUserType (args[i]);
        	OutputVisitor v = new OutputVisitor();
        	t.container().accept (v);
	      }
	    }
    } 
    else
    {
      System.out.println ("Barat version " + Version.value());
    }
  }
}
