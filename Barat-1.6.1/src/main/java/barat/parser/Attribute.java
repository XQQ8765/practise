/*
 * File: Attribute.java
 *
 * $Id: Attribute.java,v 1.11 2000/11/20 01:51:02 bokowski Exp $
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
import barat.reflect.AUserType;
import barat.AbstractAttribute;
import barat.Node;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;

abstract public class Attribute<A> implements AbstractAttribute
{
    public static class CyclicAttributeException extends RuntimeException
    {
	public CyclicAttributeException(String s)
	{
	    super(s);
	}
    }

  abstract protected A calculate();
  
  private A value;
  protected Node container = null;
  private String aspect = null;
  private boolean calculating = false;

  public void setupContainment(Node n, String s)
  {
        container = n;
        aspect = s;
        if ((value != null) && (value instanceof Node) && (container!=null) && (!(value instanceof AType) || (value instanceof AUserType))) // quick fixes :(
          ((NodeImpl)value).setupContainment (container, aspect);
  }

  public Object objectValue()
  {
    // untyped version of value(), declared in AbstractAttribute
    return value();
  }

  // override this method only when you know what you're doing!
  // hint: this is for breaking cycles in a way that preserves the
  //       values that have been calculated so far (the default
  //       behavior is to throw an exception, which causes all
  //       values that are currently evaluating to be set to null.)
  // (ask Boris...)
  protected A calculateInCaseOfCycle() {
    throw new CyclicAttributeException("");
  }

  static class StoredTag {
    List<String> tags;
    Scope        scope;
    
    StoredTag(List<String> tags, Scope scope) {
      this.tags  = tags;
      this.scope = scope;
    }
  }

  /* To save memory this is not implemented as an per-instance field,
   * but through a map that stores tags associated with a certain
   * attribute, e.g. an Expression.
   */
  static Map<Attribute, StoredTag> stored_attribute_tags = new HashMap<Attribute, StoredTag>();

  public A value()
  {
    if(value==null)
    {
        //A[] as = new A[0];
        //Class c = as.getClass().getComponentType();
        //System.out.println("calculating value of type " + c.getName());

        if(calculating) {
	  value = calculateInCaseOfCycle();
	} else {
	  try {
	    calculating = true;
	    value = calculate();
	  }
	  finally {
	    calculating = false;
	  }
	}

        if((container != null) && (value instanceof Node) && (!(value instanceof AType) || (value instanceof AUserType))) // quick fixes :(
        {
                ((Node)value).setupContainment(container, aspect);
        }
        
        //System.out.println("value is: " + value);

	StoredTag t = stored_attribute_tags.get(this);

	if(t != null) {
	  //System.out.println("RETRIEVE: " + this + ":" + t + ":" + value);
	  for(Iterator<String> i = t.tags.iterator(); i.hasNext();)
	    ((NodeImpl)value).addTag(i.next(), t.scope);

	  stored_attribute_tags.remove(this);
	}
    }
    
    return value;
  }
  
  boolean hasValue()
  {
  	return value!=null;
  }
  
  public static <A> A getValue(Object o, Node n)
  {
    NodeImpl ni = (NodeImpl)n;
    Attribute<A> ra = (Attribute)ni.getAttribute(o);
    if(ra==null) return null;
    return ra.value();
  }
}



