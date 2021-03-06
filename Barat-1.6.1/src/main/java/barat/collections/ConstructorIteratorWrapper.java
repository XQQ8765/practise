/*
 * File: IteratorWrapper.generic
 *
 * $Id: IteratorWrapper.generic,v 1.6 2000/11/20 01:50:59 bokowski Exp $
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

package barat.collections;

import barat.reflect.Constructor;

/**
 * A wrapper around ListIterators as found in JDK 1.2, to
 * make them generic.  This class is not intended for users.
 *
 * Author: Andre Spiegel
 *
 */
public class ConstructorIteratorWrapper implements ConstructorListIterator {

  private java.util.ListIterator real = null;

  public ConstructorIteratorWrapper (ConstructorList forWhat) {
    super();
    real = ((java.util.List)(forWhat.castUp())).listIterator ();
  }

  public ConstructorIteratorWrapper (ConstructorList forWhat, int index) {
    super();
    real = ((java.util.List)(forWhat.castUp())).listIterator (index);
  }

  // public methods

  public boolean hasNext() {
    return real.hasNext();
  }

  public Constructor next() {
    return (Constructor)real.next();
  }

  public boolean hasPrevious() {
    return real.hasPrevious();
  }

  public Constructor previous() {
    return (Constructor)real.previous();
  }

  public int nextIndex() {
    return real.nextIndex();
  }

  public int previousIndex() {
    return real.previousIndex();
  }

  public void remove() {
    real.remove();
  }

  public void set (Constructor o) {
    real.set (o);
  }

  public void add (Constructor o) {
    real.add (o);
  }

}
