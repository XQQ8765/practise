/*
 * File: PMGpp2.java
 *
 * $Id: PMGpp2.java,v 1.4 2002/01/28 10:50:08 dahm Exp $
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

import java.io.*;
import java.util.Vector;

/**
 * Preprocessor for PMG, converts identifiers in Pizza notation
 * into PMG identifiers. Takes package and import statements into account
 * in order to create fully qualified identifiers.
 *
 * Line numbers and layout of the output should be pretty much the
 * same as of the input file.
 *
 * @author <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
public class PMGpp2 {
  /**
   * Extends ByteArrayInputStream to make 'unreading' chars possible.
   */
  private static final class MyByteArrayInputStream extends ByteArrayInputStream {
    MyByteArrayInputStream(byte[] data) { super(data); }
    final int  mark()                   { return pos; }
    final void reset(int p)             { pos = p; }
    final void unread()                 { if(pos > 0) pos--; }
  }

  /**
   * Extends ByteArrayOutputStream to make 'unwriting' chars possible.
   */
  private static final class MyByteArrayOutputStream extends ByteArrayOutputStream {
    MyByteArrayOutputStream() { super(); }
    final int  mark()         { return count; }
    final void reset(int c)   { count = c; }
    final void unwrite()      { if(count > 0) count--; }
  }

  private MyByteArrayInputStream  in;
  private MyByteArrayOutputStream my_out;

  private PrintStream  out;
  private OutputStream real_out;
  private String       package_name; // == "", if default package
  
  private static char sep = File.separatorChar;

  /**
   * Create preprocessor.
   *
   * @param in input stream to read from
   * @param out the output stream (usually a file)
   */
  public PMGpp2(InputStream in, OutputStream out) throws IOException
  {
    int             size = in.available();
    byte[]          data = new byte[size];
    DataInputStream d    = new DataInputStream(in);

    /* Put everything into a byte array.
     */
    d.readFully(data);
    d.close(); in.close();

    this.in       = new MyByteArrayInputStream(data);
    this.real_out = out;
    this.out      = new PrintStream(my_out = new MyByteArrayOutputStream());
  }

  /**
   * Convert a Pizza name into a fully qualified Pizza name
   */
  private final String fullyQualify(String name, boolean in_param) {
    String[]     components = getPizzaComponents(name);
    StringBuffer buf        = new StringBuffer();

    for(int j=0; j < components.length; j++) {
      String s = components[j];
      if(isPizzaName(s)) {
	String   class_name = getPizzaName(s);
	String[] params     = getPizzaArgs(s);
	int      size       = params.length;

	StringBuffer args = new StringBuffer("$1");
	for(int i=0; i < size; i++) { // Qualify all parameters separately
	  args.append(fullyQualify(params[i], true));

	  if(i < size - 1)
	    args.append("$3"); //$1$2
	}
	args.append("$2");

	/* Qualify class name since all arguments have been expanded
	 * by now.
	 */
	String a = args.toString();

	if(in_param && (j==0)) { // first argument of parameter list?
	  String q = class_name + a;
	  int    i = q.indexOf("$1");

	  class_name = /*util.mangle__*/(q.substring(0, i));
	}

	buf.append(class_name + a);

	/* TODO: How can the PP possibly distinguish between inner classes
	 * Map<K,V>.Entry<K,V> (. -> $) and static fields Map<K,V>.field (. -> .) ?
	 */
	if(j < components.length - 1)
	  if(in_param)
	    buf.append('$');
	  else
	    buf.append('.');
      }
      else
	buf.append(s);
    }

    return buf.toString();
  }

  /**
   * Test for chars allowed in Java identifiers
   */
  private static final boolean isLetter(int ch) {
    return
      ('\u0024' == ch) ||
      (('\u0041' <= ch) && (ch <= '\u005a')) ||
      ('\u005f' == ch) ||
      (('\u0061' <= ch) && (ch <= '\u007a')) ||
      (('\u00c0' <= ch) && (ch <= '\u00d6')) ||
      (('\u00d8' <= ch) && (ch <= '\u00f6')) ||
      (('\u00f8' <= ch) && (ch <= '\u00ff')) ||
      (('\u0100' <= ch) && (ch <= '\u1fff')) ||
      (('\u3040' <= ch) && (ch <= '\u318f')) ||
      (('\u3300' <= ch) && (ch <= '\u337f')) ||
      (('\u3400' <= ch) && (ch <= '\u3d2d')) ||
      (('\u4e00' <= ch) && (ch <= '\u9fff')) ||
      (('\uf900' <= ch) && (ch <= '\ufaff'));
  }

  /**
   * Test for digits allowed in Java identifiers
   */
  private static final boolean isDigit(int ch) {
    return
      (('\u0030' <= ch) && (ch <= '\u0039')) ||
      (('\u0660' <= ch) && (ch <= '\u0669')) ||
      (('\u06f0' <= ch) && (ch <= '\u06f9')) ||
      (('\u0966' <= ch) && (ch <= '\u096f')) ||
      (('\u09e6' <= ch) && (ch <= '\u09ef')) ||
      (('\u0a66' <= ch) && (ch <= '\u0a6f')) ||
      (('\u0ae6' <= ch) && (ch <= '\u0aef')) ||
      (('\u0b66' <= ch) && (ch <= '\u0b6f')) ||
      (('\u0be7' <= ch) && (ch <= '\u0bef')) ||
      (('\u0c66' <= ch) && (ch <= '\u0c6f')) ||
      (('\u0ce6' <= ch) && (ch <= '\u0cef')) ||
      (('\u0d66' <= ch) && (ch <= '\u0d6f')) ||
      (('\u0e50' <= ch) && (ch <= '\u0e59')) ||
      (('\u0ed0' <= ch) && (ch <= '\u0ed9')) ||
      (('\u1040' <= ch) && (ch <= '\u1049'));
  }

  /**
   * Test for white space.
   */
  private final static boolean isWS(char ch) {
    return (ch == ' ') || (ch == '\t') || (ch == '\r') ||
      (ch == '\n') || (ch == '\f');
  }

  /**
   * Matches comment starting with /,/ or /,*.
   *
   * The leading / has already been read and will be made `unread()' if this not 
   * really a comment statement.
   */
  private final boolean matchComment() {
    int ch = in.read();

    if(ch == '/') { // Single line comment, read everything until '\n'
      out.print("//");

      while((ch = in.read()) != -1) {
	out.print((char)ch);

	if(ch == '\n')
	  break;
      }

      return true;
    }
    else if(ch == '*') { // Multi-line comment, find closing */
      out.print("/*");

      boolean expect_slash = false;

    loop:
      while((ch = in.read()) != -1) {
	out.print((char)ch);

	switch(ch) {
	case '*': // If next char is a /, the comment is closed
	  expect_slash = true; 
	  break;
	  
	case '/': // Previous char has been a *
	  if(expect_slash)
	    break loop;
	  // else fall thru
	default:  
	  expect_slash = false; // Continue;
	}
      }

      return true;
    }
    else { // Not a comment
      in.unread();
      return false;
    }
  }

  /**
   * Matches string enclosed in ""
   *
   * The leading " has already been read.
   */
  private final void matchString() {
    boolean escape = false;
    int ch;

    out.print('"');

  loop:
    while((ch = in.read()) != -1) {
      out.print((char)ch);
      
      switch(ch) {
      case '\\':
	escape = true;
	break;
      case '"':
	if(!escape)
	  break loop;
      default: // fall thru
	escape = false;
      }
    }
  }

  /**
   * Matches white space and also comments, for convenicence.
   */
  private final void matchWS() {
    int ch;

    while((ch = in.read()) != -1) {
      if(isWS((char)ch))
	out.print((char)ch);
      else if(ch == '/') {
	if(!matchComment()) { // Is a comment?
	  in.unread();
	  return;
	}
      }
      else {
	in.unread();
	return;
      }
    }
  }

  /**
   * Match a simple identifier such as abc12_
   */
  private final void matchIdent(StringBuffer buf) {
    int ch;

    matchWS();

    if(((ch = in.read()) == -1) || !isLetter(ch)) {
      in.unread();
      return;
    }

    do {
      buf.append((char)ch);
      out.print((char)ch);
      ch = in.read();
    } while((ch != -1) && (isLetter(ch) || isDigit(ch)));

    if(ch != -1)
      in.unread();
  }

  /**
   * Match a fully qualified identifier such as pmg.pmgjava.PMGpp.out
   */
  private final void matchQualifiedIdent(StringBuffer buf) {
    int ch;

    do {
      matchIdent(buf);
      matchWS();

      if((ch = in.read()) == '.') {
	buf.append('.');
	out.print('.');
      }
      else {
	in.unread();
	ch = -1;
      }
    } while(ch != -1);
  }

  /**
   * Match a Pizza identifier such as Hashtable<Stack<A>, B>
   */
  private final void matchPizzaIdent(StringBuffer buf) {
    int ch=-1, m1, m2;

    do {
      matchIdent(buf);
      
      m1 = in.mark(); m2 = my_out.mark();
      matchWS();

      if((ch = in.read()) == '<') { // Parameterized?
	buf.append('<');
	out.print('<');
	matchPizzaIdent(buf);
	matchWS();

	while((ch = in.read()) == ',') { // List of parameters
	  buf.append(',');
	  out.print(',');
	  matchWS();
	  matchPizzaIdent(buf); // Recursive call
	}

	if(ch == '>') { // End of parameter list
	  buf.append('>');
	  out.print('>');
	}
	else
	  in.unread(); // error case
      }
      else {
	in.reset(m1);
	my_out.reset(m2);
      }
	//in.unread();

      m1 = in.mark(); m2 = my_out.mark();
      matchWS();
      
      if((ch = in.read()) == '.') {
	buf.append('.');
	out.print('.');
      }
      else {
	in.reset(m1);
	my_out.reset(m2);
	ch = -1;
      }
    } while(ch != -1);
  }

  public static final boolean isPizzaName(String s) {
    int i = s.indexOf('<');
    return (i > 0) && (s.lastIndexOf('>') > i);
  }

  /**
   * Parse the given for Pizza identifiers and map them to PMG identifiers.
   */
  public void parse() throws IOException {
    int          ch;
    StringBuffer buf = new StringBuffer();

    /* Now do the real parsing.
     */    
    while((ch = in.read()) != -1) {
      if(isLetter(ch)) { // Found an identifier?
	in.unread();
	buf.setLength(0); // Empty buffer

	int m = my_out.mark(); // Remember current position
	matchPizzaIdent(buf);

	String s = buf.toString();

	if(isPizzaName(s)) {
	  my_out.reset(m); // identifier needs to be replaced
	  String fq = fullyQualify(s, false);
 	  out.print(fq);

	  /* The class file will be needed anyway, so look it up and
	   * compile it in advance if necessary.
	   * If the name doesn't end with $$ it may be a class, field or method.
	   * Since we don't know, we strip it off and jope for the best/
	   */
	  if(!fq.endsWith("$2")) {
	    int index = fq.lastIndexOf('$');
	    fq = fq.substring(0, index + 1);
	  }

	  //	  try { qualify(fq); } catch(SubstitutionException ex) {} // Don't care here
	}
      }
      else if(ch == '/') { // Check for comment
	if(!matchComment())
	  out.print((char)ch);
      }
      else if(ch == '"') {
	matchString();
      }
      else // Don't care
	out.print((char)ch);
    }

    my_out.writeTo(real_out); // Print buffered data
  }


  public static final String getPizzaName(String name) {
    return name.substring(0, name.indexOf('<'));
  }

  public static final String[] getPizzaArgs(String name) {
    String s    = name.substring(name.indexOf('<') + 1, name.length() - 1);
    int    size = s.length();
    Vector vec  = new Vector();

    try {
      StringBuffer buf   = new StringBuffer();
      int          level = 0;

      for(int i=0; i < size; i++) {
        char ch = s.charAt(i);

        if(ch == '<')
          level++;
        else if(ch == '>')
          level--;

        if((ch == ',') && (level == 0)) {
          vec.addElement(buf.toString().trim());
          buf.setLength(0);
        }
        else
          buf.append(ch);
      }
      vec.addElement(buf.toString().trim());

    } catch(StringIndexOutOfBoundsException e) { // Never occurs
      e.printStackTrace();
    }
    
    String args[] = new String[vec.size()];
    vec.copyInto(args);

    return args;
  }

  /**
   * Return components of Pizza name, e.g.
   * Map<K,V>.Entry<K.X,V>.b is separated into
   * [Map<K,V>, Entry<K.X,V>, b]
   *
   * PRE: isPizzaName(name)
   */
  static final String[] getPizzaComponents(String s) {
    int    size = s.length();
    char[] name = new char[size];
    Vector vec  = new Vector();

    s.getChars(0, size, name, 0);

    try {
      StringBuffer buf    = new StringBuffer();
      int          level  = 0;
      boolean      opened = false;

      for(int i=0; i < size; i++) {
	char ch = name[i];

	if(ch == '<') {
	  opened = true;
	  level++;
	}
	else if(ch == '>')
	  level--;

	if((ch == '.') && (level == 0) && opened) {
	  vec.addElement(buf.toString().trim());
	  buf.setLength(0);
	  opened = false;
	}
	else
	  buf.append(ch);
      }
      vec.addElement(buf.toString().trim());

    } catch(StringIndexOutOfBoundsException e) { // Never occurs
      e.printStackTrace();
    }
    
    String args[] = new String[vec.size()];
    vec.copyInto(args);

    return args;
  }

  /**
   * Test program
   */
  public static void main(String args[]) {
    String[] files = args;

    try {
      for(int i=0; i < files.length; i++) {
	FileInputStream f   = new FileInputStream(files[i]);
	OutputStream    out = System.out;
	PMGpp2           parser = new PMGpp2(f, out);
	parser.parse();
      }
    } catch(Exception e) { e.printStackTrace(); }
  }
}
