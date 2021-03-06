/*
 * File: Modifiers.java
 *
 * $Id: Modifiers.java,v 1.5 2000/11/20 01:51:07 bokowski Exp $
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

package barat.parser;

/* generated by Boris Bokowski's rosegen on Sat Jul 11 18:34:17 GMT+03:30 1998
*/


public class Modifiers implements java.io.Serializable
{
	private boolean isAbstract = false;
	private boolean isFinal = false;
	private boolean isNative = false;
	private boolean isPrivate = false;
	private boolean isProtected = false;
	private boolean isPublic = false;
	private boolean isStatic = false;
	private boolean isSynchronized = false;
	private boolean isTransient = false;
	private boolean isVolatile = false;
	private boolean isStrictfp = false;
	public Modifiers( )
	{
		super( );
	}
	public boolean isAbstract()
	{
		return isAbstract;
	}
	public void isAbstract(boolean isAbstract_)
	{
		isAbstract = isAbstract_;
	}
	public boolean isFinal()
	{
		return isFinal;
	}
	public void isFinal(boolean isFinal_)
	{
		isFinal = isFinal_;
	}
	public boolean isNative()
	{
		return isNative;
	}
	public void isNative(boolean isNative_)
	{
		isNative = isNative_;
	}
	public boolean isPrivate()
	{
		return isPrivate;
	}
	public void isPrivate(boolean isPrivate_)
	{
		isPrivate = isPrivate_;
	}
	public boolean isProtected()
	{
		return isProtected;
	}
	public void isProtected(boolean isProtected_)
	{
		isProtected = isProtected_;
	}
	public boolean isPublic()
	{
		return isPublic;
	}
	public void isPublic(boolean isPublic_)
	{
		isPublic = isPublic_;
	}
	public boolean isStatic()
	{
		return isStatic;
	}
	public void isStatic(boolean isStatic_)
	{
		isStatic = isStatic_;
	}
	public boolean isSynchronized()
	{
		return isSynchronized;
	}
	public void isSynchronized(boolean isSynchronized_)
	{
		isSynchronized = isSynchronized_;
	}
	public boolean isTransient()
	{
		return isTransient;
	}
	public void isTransient(boolean isTransient_)
	{
		isTransient = isTransient_;
	}
	public boolean isVolatile()
	{
		return isVolatile;
	}
	public void isVolatile(boolean isVolatile_)
	{
		isVolatile = isVolatile_;
	}
	public boolean isStrictfp()
	{
		return isStrictfp;
	}
	public void isStrictfp(boolean isStrictfp_)
	{
		isStrictfp = isStrictfp_;
	}
}
