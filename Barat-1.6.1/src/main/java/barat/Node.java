/*
 * File: Node.java
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

import barat.collections.*;
import barat.reflect.AExpression;
import barat.reflect.ATyped;
import barat.reflect.AUserType;
import barat.reflect.AMethod;

/**
 * Super class for all nodes of the Barat abstract syntax tree (AST).
 *
 * @version $Id: Node.java,v 1.19 2000/11/20 01:50:51 bokowski Exp $
 */
public interface Node extends java.io.Serializable {
  /** @return line number for a node (may not be entirely accurate)
   */
  public int line_number();

  // for retrieving a node's file name, in which the line
  // number is valid, use the following:
  // ((CompilationUnit)node.containing(CompilationUnit.class)).filename()
  
  // container and containment aspect of this node:

  /** An aspect of a node is for example "lvalue" for an expression.
   */
  public String aspect();

  /** @return the container of a node, e.g., the container of a local variable is a method
   */
  public Node container();

  /** @return true if this node contains that node (transitively)
   */
  public boolean contains(Node other);

  /** Helper method for traversing the container chain
   */
  public Node containing(java.lang.Class ofClass);

  /** @return containing class of node
   */
  public barat.reflect.Class containingClass();

  /** @return containing user type of node
   */
  public barat.reflect.AUserType containingUserType();

  /** @return containing method of node if any
   */
  public barat.reflect.AMethod containingMethod();
  
  // ----------"new" tag methods (JavaDoc comment tags)

  /** This method returns true if there is a JavaDoc tag with name t for this node.
   *  The method name is likely to change once the "old" tag methods are removed.
   */
  public boolean new_hasTag(String t);

  /** This method returns the array of JavaDoc tags for this node or null if this node has no tags.
   *  The method name is likely to change once the "old" tag methods are removed.
   */
  public Tag[] new_getTags(); // May be null

  /** This method returns the JavaDoc tag with name t for this node or null if there is no such tag.
   *  The method name is likely to change once the "old" tag methods are removed.
   */
  public Tag new_getTag(String tagName); // May be null

  /** This method returns the value of the JavaDoc tag with name t for this node or null if there is no such tag.
   *  The method name is likely to change once the "old" tag methods are removed.
   */
  public String new_getTagValue(String tagName); // May be null
  
  /** Parse a String as an expression in this node's scope.
   *  Do not call this method while the parser is being used.
   */
  public AExpression parseExpr(String source);

  /** Lookup a variable (Field or AVariable) by the given name in the scope of this node.
   *  The variable name must be unqualified.
   *  Returns null if no variable of the given name was found.
   */
  public ATyped lookupVariable(String name);

  /** Lookup a user type by the given name in the scope of this node.
   *  The type name can be unqualified or qualified.
   *  Returns null if no type of the given name was found.
   */
  public AUserType lookupUserType(String name);

  // --------- ("old" tag methods) access to tags (/*:special*/ comments)

  /**@deprecated. Use the "new" tag methods instead.*/
  public boolean hasTag(String t);

  /**@deprecated. Use the "new" tag methods instead.*/
  public StringList getTags(); // May be null

  /**@deprecated. Use the "new" tag methods instead.*/
  public AExpression getTagAsExpr(String source, int i);

  // defining attributes and retrieving attribute values:

  /** Add an attribute to this node which may be retrieved via the key object.
   */
  public void addAttribute(Object key, AbstractAttribute a);

  /** @return attribute value found via the key object
   */
  public Object attributeValue(Object key);

  /** Visitor method for this node.
   */
  public void accept(Visitor v);

  //****************************************************
  // these methods should be removed from this interface,
  // as they may be called by the implementation classes
  // in barat.parser only:
  
  /**@deprecated. This method will be removed from this interface.*/
  public void line_number(int line);
  
  /**@deprecated. This method will be removed from this interface.*/
  public void setupContainment(Node container_, String aspect);
}
