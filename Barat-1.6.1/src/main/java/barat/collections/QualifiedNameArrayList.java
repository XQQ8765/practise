/*
 * File: ArrayList.generic
 *
 * $Id: ArrayList.generic,v 1.6 2000/11/20 01:50:59 bokowski Exp $
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

import barat.QualifiedName;

/**
 * This is a hand-crafted generic wrapper of classes
 * java.util.AbstractCollection / AbstractList / ArrayList 
 * found in JDK 1.2.
 *
 * Based on: java/util/AbstractCollection.java  1.9  98/03/18
 * Based on: java/util/AbstractList.java        1.16 98/03/18
 * Based on: java/util/ArrayList.java           1.10 98/03/18
 * Author:   Sun Microsystems / Andre Spiegel
 *
 */
public class QualifiedNameArrayList implements QualifiedNameList, Cloneable, java.io.Serializable {

    // the real thing

    private java.util.ArrayList real = null;

    // constructors (adapted from ArrayList)

    public QualifiedNameArrayList(int initialCapacity) {
        super();
        real = new java.util.ArrayList (initialCapacity);
    }

    public QualifiedNameArrayList() {
	super();
        real = new java.util.ArrayList ();
    }

    public QualifiedNameArrayList(QualifiedNameCollection c) {
        super();
        real = new java.util.ArrayList (c.castUp());
    }

    // code from AbstractCollection below this line

    public Object[] toArray(Object a[]) {
        return real.toArray (a);
    }

    public boolean remove(QualifiedName o) {
        return real.remove (o);
    }

    public boolean containsAll(QualifiedNameCollection c) {
        return real.containsAll (c.castUp());
    }

    public boolean removeAll(QualifiedNameCollection c) {
        return real.removeAll (c.castUp());
    }

    public boolean retainAll(QualifiedNameCollection c) {
        return real.retainAll (c.castUp());
    }

    public String toString() {
        return real.toString();
    }

    // code from AbstractList below this line

    public QualifiedNameIterator iterator() {
	return new QualifiedNameIteratorWrapper (this);
    }

    public QualifiedNameListIterator listIterator() {
	return new QualifiedNameIteratorWrapper (this);
    }

    public QualifiedNameListIterator listIterator(final int index) {
	return new QualifiedNameIteratorWrapper (this, index);
    }

    public boolean equals(Object o) {
        return real.equals (o);
    }

    public int hashCode() {
	return real.hashCode();
    }

    // code from ArrayList below this line

    public void trimToSize() {
        real.trimToSize();
    }

    public void ensureCapacity(int minCapacity) {
        real.ensureCapacity (minCapacity);
    }

    public int size() {
        return real.size();
    }

    public boolean isEmpty() {
        return real.isEmpty();
    }

    public boolean contains(QualifiedName elem) {
        return real.contains (elem);
    }

    public int indexOf(QualifiedName elem) {
        return real.indexOf (elem);
    }

    public int lastIndexOf(QualifiedName elem) {
        return real.lastIndexOf (elem);
    }

    public Object clone() {
        return new QualifiedNameArrayList (this);
    }

    public QualifiedName[] toArray() {
        Object[] realResult = real.toArray();
	QualifiedName[] result = new QualifiedName[real.size()];
	System.arraycopy(realResult, 0, result, 0, real.size());
	return result;
    }

    public QualifiedName get(int index) {
        return (QualifiedName)real.get (index);
    }

    public QualifiedName set(int index, QualifiedName element) {
        return (QualifiedName)real.set (index, element);
    }

    public boolean add(QualifiedName o) {
        return real.add (o);
    }

    public void add(int index, QualifiedName element) {
        real.add (index, element);
    }

    public QualifiedName remove(int index) {
        return (QualifiedName)real.remove (index);
    }

    public void clear() {
        real.clear();
    }

    public boolean addAll(QualifiedNameCollection c) {
        return real.addAll (c.castUp());
    }

    public boolean addAll(int index, QualifiedNameCollection c) {
        return real.addAll (index, c.castUp());
    }

    public java.util.Collection castUp() {
        return real;
    }

}
