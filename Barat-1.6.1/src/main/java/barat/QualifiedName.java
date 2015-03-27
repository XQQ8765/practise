/*
 * File: QualifiedName.java
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

import barat.collections.StringIterator;

/**
 * Represents a fully qualified name.
 *
 * @version $Id: QualifiedName.java,v 1.5 2000/11/20 01:50:51 bokowski Exp $
 */
public class QualifiedName implements java.io.Serializable {
  private String        baseName;
  private QualifiedName qualifier;

  public static QualifiedName from(String s) {
    int index = s.lastIndexOf('.');

    if(index==-1)
      return new QualifiedName(s, null);
    else
      return new QualifiedName(s.substring(index+1), QualifiedName.from(s.substring(0,index)));
  }

  public QualifiedName( String baseName_, QualifiedName qualifier_) {
    super( );
    baseName = baseName_;
    qualifier = qualifier_;
  }

  public QualifiedName() {}

  public void setBaseName( String baseName_ ) {
    baseName = baseName_;
  }

  public String getBaseName() {
    return baseName;
  }

  public void setQualifier(QualifiedName qualifier_ ){
    qualifier = qualifier_;
  }

  public QualifiedName getQualifier() {
    return qualifier;
  }

  public boolean isQualified(){
    return qualifier!=null;
  }

  public StringIterator iterator() {
    if(qualifier==null) {
      return new StringIterator() {
	private boolean hasNext = true;
	public boolean hasNext() { return hasNext; }
              public String next() { hasNext = false; return baseName; }
              public void remove() { throw new RuntimeException("not possible"); }
      };
    } else {
      return new StringIterator() {
	private StringIterator qualIter = qualifier.iterator();
	private boolean hasNext = true;
	public boolean hasNext() { return qualIter.hasNext() || hasNext; }
	public String next() { if(qualIter.hasNext()) return qualIter.next();
	else { hasNext = false; return baseName; } }
	public void remove() { throw new RuntimeException("not possible"); }
      };
    }
  }

  public String toString() {
    StringIterator si = iterator();
    StringBuffer result = new StringBuffer(si.next());

    while(si.hasNext()) {
      result.append('.');
      result.append(si.next());
    }

    return result.toString();
  }
}

