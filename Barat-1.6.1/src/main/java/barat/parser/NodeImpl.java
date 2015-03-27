/*
 * File: NodeImpl.java
 *
 * $Id: NodeImpl.java,v 1.14 2000/11/20 01:51:07 bokowski Exp $
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

import barat.collections.*;
import java.io.StringWriter;
import barat.*;
import barat.reflect.*;
import java.util.*;

public abstract class NodeImpl implements Node {
  private Node container = null;
  private String containmentAspect = null;
	
  private int line_number = -1;
        
  public void line_number(int l)
  {
    line_number = l;
  }
        
  public int line_number()
  {
    if(line_number==-1)
      {
	if(container()==null)
	  {
	    return -1;
	  }
	else
	  {
	    return container.line_number();
	  }
      }
    else
      {
	return line_number;
      }
  }

  public NodeImpl()
  {
  }
  abstract public void accept(barat.parser.ImplementationVisitor v);
  public String aspect()
  {
    return containmentAspect;
  }			

  public boolean contains(Node other) {
    return NameAnalysis.contains(this, other);
  }

  public Node container()
  {
    return container;
  }

  public Node containing(java.lang.Class ofClass)
  {
    if (container==null) return null;
    if (ofClass.isInstance(container))
      {
	return container;
      }
    else
      {
	// When seeking for something that is inside a type,
	// don't wander into outer types.
	if (container instanceof barat.reflect.AUserType) {
	  if ((ofClass != barat.reflect.Package.class) &&
	      (ofClass != barat.parser.PackageImpl.class) &&
	      (ofClass != barat.reflect.CompilationUnit.class) &&
	      (ofClass != barat.parser.CompilationUnitImpl.class) &&
	      (!barat.reflect.AUserType.class
	       .isAssignableFrom (ofClass)))
	    return null;
	}
	Node n = (Node)container;
	return n.containing(ofClass);
      }	
  }	
  public barat.reflect.Class containingClass()
  {
    return (barat.reflect.Class)
      containing (barat.reflect.Class.class);
  }	
  public barat.reflect.AUserType containingUserType()
  {
    return (barat.reflect.AUserType)
      containing (barat.reflect.AUserType.class);
  }	
  public barat.reflect.AMethod containingMethod()
  {
    return (barat.reflect.AMethod)
      containing (barat.reflect.AMethod.class);
  }	
  public void setupContainment(Node container_, String aspect)
  {
    if(container_==null)
      throw new RuntimeException ("error: null containment");
    if(container!=null)
      {
	throw new RuntimeException("error: multiple containment");
      }
    container=container_;
    containmentAspect=aspect;
  }
	
  private Map<Object, AbstractAttribute> attributes
    /*= new HashMap<Object, AbstractAttribute>()*/; // user-defined attributes
  public void addAttribute(Object o, AbstractAttribute a)
  {
    if(attributes == null)
      attributes = new HashMap<Object, AbstractAttribute>(3);
    attributes.put(o, a);
  }
  public Object attributeValue(Object key)
  {
    // this is an untyped version of Attribute<A>.getValue()
    AbstractAttribute a = getAttribute(key);
    if(a==null) return null;
    return a.objectValue();
  }
  public AbstractAttribute getAttribute(Object o)
  {
    if(attributes != null)
      return attributes.get(o);
    return null;
  }
  
  // this is the "new" tag mechanism, based on parsing JavaDoc comments
  // and the tags contained in them. I hope that these can replace the
  // "old" tags soon... (BB, 2000-06-23)
  
  private Tag[] new_tags = null;
  
  /** This method is used internally.
   *  The method name is likely to change once the "old" tag methods are removed.
   */
  void new_setTags(Tag[] tags)
  {
  	new_tags = tags;
  }

  /** This method returns all tags for this node. 
   *  The method name is likely to change once the "old" tag methods are removed.
   */
  public Tag[] new_getTags()
  {
    return new_tags;
  }
  
  /** This method returns true if there is a JavaDoc tag with name t.
   *  The method name is likely to change once the "old" tag methods are removed.
   */
  public boolean new_hasTag(String t)
  {
    return new_getTag(t)!=null;
  }

  /** This method returns a tag with this name or null if no tag with this name exists.
   *  The method name is likely to change once the "old" tag methods are removed.
   */
  public Tag new_getTag(String tagName)
  {
  	if(new_tags==null) return null;
  	for(int i=0; i<new_tags.length; i++)
	{
		if(tagName.equals(new_tags[i].getName())) return new_tags[i];
	}
	return null;
  }
  
  /** This method returns the value of a tag with this name or null if no tag with this name exists.
   *  The method name is likely to change once the "old" tag methods are removed.
   */
  public String new_getTagValue(String tagName)
  {
  	Tag t = new_getTag(tagName);
	if(t==null) return null;
	return t.getValue();
  }

  protected Scope getScope() {
    Scope scope = (Scope)containing(Scope.class);
    if(scope==null) throw new RuntimeException("could not determine scope");
    if(scope instanceof Block)
    {
	    // we have to traverse the tree to find local declarations
	    // in the block if there are any.
	    Node containedInBlock = this;
	    while(containedInBlock.container()!=scope)
	    {
		    containedInBlock=containedInBlock.container();
	    }
	    // now containedInBlock is a child of scope
	    for(AStatementIterator i=((Block)scope).getStatements().iterator(); i.hasNext();)
	    {
		    AStatement s = i.next();
		    if(s==containedInBlock) break;
		    if(s instanceof Scope) scope = (Scope)s;
	    }
    }
    return scope;
  }

  /** Parse a String as an expression in this node's scope.
   *  Do not call this method while the parser is being used.
   */
  public AExpression parseExpr(String source)
  {
	return internalParseExpr(source, getScope());
  }

  public AUserType lookupUserType(String name) {
    if(name.indexOf('.')!=-1) {
      return NameAnalysis._lookupUserType(getScope(), name);
    } else {
      return NameAnalysis._lookupUserType(getScope(), QualifiedName.from(name));
    }
  }
  
  public ATyped lookupVariable(String name) {
    return NameAnalysis.lookupATyped(getScope(), name, false);
  }

  private StringList tags/* = new StringArrayList()*/;
  private java.util.List<Scope> tagScopes
    /* = new java.util.ArrayList<Scope>()*/;

  /** @deprecated. See "new" tags above */
  public void addTag(String t, Scope scope)
  {
    if(tags == null) {
      tags      = new StringArrayList(3);
      tagScopes = new java.util.ArrayList<Scope>(3);
    }

    tags.add(t);
    tagScopes.add(scope);
  }

  /** @deprecated. See "new" tags above */
  public boolean hasTag(String t)
  {
    return (tags == null)? false : tags.contains(t);
  }

  /** @deprecated. See "new" tags above */
  public StringList getTags()
  {
    return tags;
  }

  /** @deprecated. See "new" tags above */
  public AExpression getTagAsExpr(String source, int i)
  {
	return internalParseExpr(source!=null?source:tags.get(i), tagScopes.get(i));
  }
  
  /** This method parses exprString in the given Scope and returns an AExpression. */
  private AExpression internalParseExpr(String exprString, Scope scope)
  {
    Attribute<AExpression> expr = null;
    if(NameAnalysis.parserInUse) throw new RuntimeException("Parser in use! (a special tag is parsed or what?)");
    NameAnalysis.parserInUse = true;
	  
    try {
      java.io.InputStream in = new java.io.StringBufferInputStream(exprString);

      if (NameAnalysis.theParser == null)
	NameAnalysis.theParser = new BaratParser (in);
      else
	BaratParser.ReInit (in);

      NameAnalysis.theParser.currentScope = scope;
      expr = NameAnalysis.theParser.Expression();
      NameAnalysis.theParser.currentScope = null;

      in.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
            
    NameAnalysis.parserInUse = false;
    return expr.value();
  }

  public String toString()
  {
    if(this instanceof barat.reflect.ANamed)
      {
	return ((barat.reflect.ANamed)this).getName();
      }
    else
      {
	StringWriter sw = new StringWriter();
	OutputVisitor ov = new OutputVisitor(sw);
	this.accept(ov);
	sw.flush();
	return sw.toString();
      }
  }
}
