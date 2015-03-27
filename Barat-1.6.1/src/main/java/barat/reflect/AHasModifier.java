/*
 * File: AHasModifier.java
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

/**
 * Denote whether a node has access modifiers such as "public" or
 * "private", fields and methods in particular.
 *
 * @version $Id: AHasModifier.java,v 1.6 2000/11/20 01:51:11 bokowski Exp $
 */
public interface AHasModifier extends Node {
  /** @return true, if node has the "abstract" modifier.
   */
  public boolean isAbstract();

  /** @return true, if node has the "final" modifier.
   */
  public boolean isFinal();

  /** @return true, if node has the "native" modifier.
   */
  public boolean isNative();

  /** @return true, if node has the "private" modifier.
   */
  public boolean isPrivate();

  /** @return true, if node has the "protected" modifier.
   */
  public boolean isProtected();

  /** @return true, if node has the "public" modifier.
   */
  public boolean isPublic();

  /** @return true, if node has the "static" modifier.
   */
  public boolean isStatic();

  /** @return true, if node has the "synchronized" modifier.
   */
  public boolean isSynchronized();

  /** @return true, if node has the "transient" modifier.
   */
  public boolean isTransient();

  /** @return true, if node has the "volatile" modifier.
   */
  public boolean isVolatile();

  /** @return true, if node has the "strictfp" modifier.
   */
  public boolean isStrictfp();
}
