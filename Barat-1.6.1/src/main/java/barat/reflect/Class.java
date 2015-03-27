/*
 * File: Class.java
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

package barat.reflect;

import barat.collections.ConcreteMethodList;
import barat.collections.ConstructorList;
import barat.collections.InterfaceList;
import barat.collections.BlockList;

/**
 * Represents a class.
 *
 * @version $Id: Class.java,v 1.15 2000/11/20 01:51:14 bokowski Exp $
 */
public interface Class extends AUserType {
  /** @return super class of this class
   */
  public Class getSuperclass();

  /** @return true if this class is a sub class of the given super class (transitively)
   */
  public boolean isSubclassOf (Class superClass);

  /** @return list of implemented interfaces
   */
  public InterfaceList getImplementedInterfaces();

  /** @return true if this class implements the given super class (transitively)
   */
  public boolean isImplementationOf (Interface superInterface); 

  /** obsolete
   * @deprecated
   */
  public BlockList getInstanceInitializers();

  /** @return list of constructors
   */
  public ConstructorList getConstructors();

  /** @return constructor with given argument types if any
   */
  public Constructor getConstructor (AType[] argTypes);

  /** @return list of concrete methods
   */
  public ConcreteMethodList getConcreteMethods();

  /** Visitor method for this node.
   */
  public void accept(barat.Visitor v);
}
