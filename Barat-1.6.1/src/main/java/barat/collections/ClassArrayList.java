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

import barat.reflect.Class;

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
public class ClassArrayList implements ClassList, Cloneable, java.io.Serializable {

    // the real thing

    private java.util.ArrayList real = null;

    // constructors (adapted from ArrayList)

    public ClassArrayList(int initialCapacity) {
        super();
        real = new java.util.ArrayList (initialCapacity);
    }

    public ClassArrayList() {
	super();
        real = new java.util.ArrayList ();
    }

    public ClassArrayList(ClassCollection c) {
        super();
        real = new java.util.ArrayList (c.castUp());
    }

    // code from AbstractCollection below this line

    public Object[] toArray(Object a[]) {
        return real.toArray (a);
    }

    public boolean remove(Class o) {
        return real.remove (o);
    }

    public boolean containsAll(ClassCollection c) {
        return real.containsAll (c.castUp());
    }

    public boolean removeAll(ClassCollection c) {
        return real.removeAll (c.castUp());
    }

    public boolean retainAll(ClassCollection c) {
        return real.retainAll (c.castUp());
    }

    public String toString() {
        return real.toString();
    }

    // code from AbstractList below this line

    public ClassIterator iterator() {
	return new ClassIteratorWrapper (this);
    }

    public ClassListIterator listIterator() {
	return new ClassIteratorWrapper (this);
    }

    public ClassListIterator listIterator(final int index) {
	return new ClassIteratorWrapper (this, index);
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

    public boolean contains(Class elem) {
        return real.contains (elem);
    }

    public int indexOf(Class elem) {
        return real.indexOf (elem);
    }

    public int lastIndexOf(Class elem) {
        return real.lastIndexOf (elem);
    }

    public Object clone() {
        return new ClassArrayList (this);
    }

    public Class[] toArray() {
        Object[] realResult = real.toArray();
	Class[] result = new Class[real.size()];
	System.arraycopy(realResult, 0, result, 0, real.size());
	return result;
    }

    public Class get(int index) {
        return (Class)real.get (index);
    }

    public Class set(int index, Class element) {
        return (Class)real.set (index, element);
    }

    public boolean add(Class o) {
        return real.add (o);
    }

    public void add(int index, Class element) {
        real.add (index, element);
    }

    public Class remove(int index) {
        return (Class)real.remove (index);
    }

    public void clear() {
        real.clear();
    }

    public boolean addAll(ClassCollection c) {
        return real.addAll (c.castUp());
    }

    public boolean addAll(int index, ClassCollection c) {
        return real.addAll (index, c.castUp());
    }

    public java.util.Collection castUp() {
        return real;
    }

}
