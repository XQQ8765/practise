/*
 * File: AUserType.java
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

import barat.collections.FieldList;
import barat.collections.AbstractMethodList;
import barat.collections.InterfaceList;
import barat.collections.ClassList;
import barat.collections.BlockList;

/**
 * Super class for classes and interfaces.
 *
 * @version $Id: AUserType.java,v 1.12 2000/11/20 01:51:12 bokowski Exp $
 */
public interface AUserType extends AHasModifier, AReferenceType, ANamed {
  /** @return list of fields
   */
  public FieldList getFields();

  /** @return list of abstract methods
   */
  public AbstractMethodList getAbstractMethods();

  /** @return list of interfaces contained within this user type (inner interfaces)
   */
  public InterfaceList getNestedInterfaces();

  /** @return list of classes contained within this user type (inner classes)
   */
  public ClassList getNestedClasses();

  /** @return list of static initializer blocks (static { ... })
   */
  public BlockList getStaticInitializers();

  /** Visitor method for this node.
   */
  public void accept(barat.Visitor v);
}
