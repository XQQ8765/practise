/*
 * File: List.generic
 *
 * $Id: List.generic,v 1.5 2000/11/20 01:50:59 bokowski Exp $
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

import barat.reflect.AStatement;

/**
 * This is a hand-crafted generic instantiation of
 * the java.util.List interface found in JDK 1.2.
 *
 * Based on: java/util/List.java  1.19 98/03/18
 * Author:   Sun Microsystems / Andre Spiegel
 *
 */
public interface AStatementList extends AStatementCollection {

    int size();

    boolean isEmpty();

    boolean contains(AStatement o);

    AStatementIterator iterator();

    AStatement[] toArray();

    boolean add(AStatement o);

    boolean remove(AStatement o);

    boolean containsAll(AStatementCollection c);

    boolean addAll(AStatementCollection c);

    boolean removeAll(AStatementCollection c);

    boolean retainAll(AStatementCollection c);

    void clear();

    boolean equals(Object o);

    int hashCode();

    AStatement get(int index);

    AStatement set(int index, AStatement element);

    void add(int index, AStatement element);

    AStatement remove(int index);

    int indexOf(AStatement o);

    int lastIndexOf(AStatement o);

    boolean addAll(int index, AStatementCollection c);

    AStatementListIterator listIterator();

    AStatementListIterator listIterator(int index);

}
