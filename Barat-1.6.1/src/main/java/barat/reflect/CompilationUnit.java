/*
 * File: CompilationUnit.java
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

import barat.Node;
import barat.collections.InterfaceList;
import barat.collections.ClassList;

/**
 * Represents a .java file that contains classes and interfaces
 *
 * @version $Id: CompilationUnit.java,v 1.13 2000/11/20 01:51:14 bokowski Exp $
 */
public interface CompilationUnit extends Node {
  /** @return list of contained interfaces
   */
  public InterfaceList getInterfaces();

  /** @return list of contained classes
   */
  public ClassList getClasses();

  /** @return file name
   */
  public String filename();

  /** @return true if this compilation unit was read from a .java file
   */
  public boolean hasSource();
  
  /** @return the fully qualified imports for this compilation unit as an array of AUserType
   */
  public AUserType[] getTypeImports();

  /** @return the wildcard imports for this compilation unit as an array of Package
   */
  public Package[] getPackageImports();

  /** Visitor method for this node.
   */
  public void accept(barat.Visitor v);
}
