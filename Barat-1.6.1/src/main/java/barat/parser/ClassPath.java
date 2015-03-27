/*
 * File: ClassPath.java
 *
 * $Id: ClassPath.java,v 1.9 2000/11/20 01:51:04 bokowski Exp $
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

import java.util.*;
import java.util.zip.*;
import java.io.*;

/**
 * Responsible for finding files on the class path.
 *
 * @author  <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 *          Modified by Andre Spiegel, based on version 
 *          1.2 1998/08/10 15:56:52 dahm
 */
public class ClassPath {
  private PathEntry[] paths;

  public ClassPath(String class_path) throws IOException {
    Vector vec = new Vector();

    for(StringTokenizer tok = new StringTokenizer(class_path, "" + File.pathSeparatorChar);
	tok.hasMoreTokens();) {
      String path = tok.nextToken();
      File   file = new File(path);

      if(file.exists()) {
	if(file.isDirectory())
	  vec.addElement(new Dir(path));
	else
	  vec.addElement(new Zip(new ZipFile(file)));
      }
    }

    paths = new PathEntry[vec.size()];
    vec.copyInto(paths);
  }

  public ClassPath() throws IOException {
    this(getClassPath());
  }

  private static final String getClassPath() {
    String class_path = System.getProperty("java.class.path");
    String boot_path  = System.getProperty("sun.boot.class.path");

    if(class_path == null)
      class_path = "";

    if(boot_path == null)
      boot_path = "";

    class_path = class_path + File.pathSeparatorChar + boot_path;

    if(class_path.charAt(0) == File.pathSeparatorChar)
      return boot_path;
    else
      return class_path;
  }

  public InputStream getJavaFile (String name) throws IOException {
    AFile f = getFile(name, true);
    if(f==null)
    {
      return null;
    }
    else
    {
      return f.getInputStream();
    }
  }

  public InputStream getClassFile (String name) throws IOException {
    AFile f = getFile(name, false);
    if(f==null)
    {
      return null;
    }
    else
    {
      return f.getInputStream();
    }
  }

  public AFile getFile (String name, boolean sourceWanted) throws IOException {
    for(int i=0; i < paths.length; i++)
    {
      AFile result;
      if((result = paths[i].getFile (name, sourceWanted)) != null)
      {
	return result;
      }
    }
    return null;
  }
  
  public String[] allFiles(String packageName)
  {
    Vector result = new Vector();
    for(int i=0; i < paths.length; i++)
    {
      paths[i].addAllFiles(packageName, result);
    }
    String[] result_ = new String[result.size()];
    result.copyInto(result_);
    return result_;
  }

  public String[] subPackages(String packageName)
  {
    Vector result = new Vector();
    for(int i=0; i < paths.length; i++)
    {
      paths[i].addSubPackages(packageName, result);
    }
    String[] result_ = new String[result.size()];
    result.copyInto(result_);
    return result_;
  }

  public static abstract class AFile
  {
    public abstract boolean exists();
    public abstract InputStream getInputStream() throws IOException;
    public abstract String getName();
    public abstract long lastModified();
  }
  
  public static class RealFile extends AFile
  {
    private File file;
    public RealFile(File f)
    {
      file = f;
    }
    public boolean exists()
    {
      return file.exists();
    }
    public InputStream getInputStream() throws IOException
    {
      return new FileInputStream(file);
    }
    public String getName()
    {
      return file.getAbsolutePath();
    }
    public long lastModified()
    {
      return file.lastModified();
    }
  }
  
  public static class ZippedFile extends AFile
  {
    private ZipFile zip;
    private ZipEntry entry;
    public ZippedFile(ZipFile z, ZipEntry e)
    {
      zip = z;
      entry = e;
    }
    public boolean exists()
    {
      return entry!=null;
    }
    public InputStream getInputStream() throws IOException
    {
      return zip.getInputStream(entry);
    }
    public String getName()
    {
      return "Zip(" + zip.getName() + "/" + entry.getName() + ")";
    }
    public long lastModified()
    {
      return entry.getTime();
    }
  }

  private abstract class PathEntry {
    protected PathEntry() {} // GJC make the constructor private otherwise !
    abstract AFile getFile (String name, boolean sourceWanted) 
      throws IOException;
    abstract void addAllFiles(String packageName, Vector result);
    abstract void addSubPackages(String packageName, Vector result);
  }
  
  private class Dir extends PathEntry {
    private String dir;

    Dir(String d) { dir = d; }

    AFile getFile (String name, boolean sourceWanted) 
      throws IOException 
    {
      File f = new File(dir + File.separatorChar
			+ name.replace('.', File.separatorChar) 
                        + (sourceWanted ? ".java" : ".class"));

      return f.exists() ? new RealFile(f) : null;
    }	

    void addAllFiles(String packageName, Vector result)
    {
      File d = new File(dir, packageName.replace('.', File.separatorChar));
      if(!d.exists() || !d.isDirectory()) return;
      String[] sourcefiles = d.list(new FilenameFilter()
        {
          public boolean accept(File dir, String name)
          {
            return name.endsWith(".java");
          }
        });
      String[] classfiles = d.list(new FilenameFilter()
        {
          public boolean accept(File dir, String name)
          {
            return name.endsWith(".class");
          }
        });
      for(int i=0; i<sourcefiles.length; i++)
      {
//System.out.println("adding " + sourcefiles[i]);
        result.addElement(sourcefiles[i]);
      }
      for(int i=0; i<classfiles.length; i++)
      {
//System.out.println("adding " + classfiles[i]);
        result.addElement(classfiles[i]);
      }
    }

    void addSubPackages(String packageName, Vector result)
    {
      File d = new File(dir, packageName.replace('.', File.separatorChar));
      if(!d.exists() || !d.isDirectory()) return;
      String[] children = d.list();
      for(int i=0; i<children.length; i++)
      {
        File child = new File(d, children[i]);
	  if(child.exists() && child.isDirectory()) {
          String newPackageName = packageName + "." + children[i];
          if(!result.contains(newPackageName)) {
            result.addElement(newPackageName);
          }
        }
      }
    }

  }

  private class Zip extends PathEntry {
    private ZipFile zip;

    Zip(ZipFile z) { zip = z; }

    AFile getFile (String name, boolean sourceWanted) 
      throws IOException 
    {
      if (sourceWanted) return null;
      ZipEntry entry = zip.getEntry(name.replace('.', '/') + ".class");

      if(entry == null)
	return null;

      return new ZippedFile(zip,entry);
    }

    void addAllFiles(String packageName, Vector result)
    {
      for(Enumeration e=zip.entries(); e.hasMoreElements();)
      {
        ZipEntry ze = (ZipEntry)e.nextElement();
//System.out.println("zip entry: " + ze.getName());
        if(ze.getName().startsWith(packageName.replace('.', '/')))
        {
          String base = ze.getName().substring(packageName.length()+1);
//System.out.println("base: " + base);
          if(base.indexOf('/')==-1 && base.endsWith(".class"))
          {
//System.out.println("adding: " + base);
            result.addElement(base);
          }
        }
      }
    }

    void addSubPackages(String packageName, Vector result)
    {
      for(Enumeration e=zip.entries(); e.hasMoreElements();)
      {
        ZipEntry ze = (ZipEntry)e.nextElement();
        if(ze.getName().startsWith(packageName.replace('.', '/')))
        {
          String rest = ze.getName().substring(packageName.length()+1);
          if(rest.indexOf('/')!=-1 && rest.endsWith(".class"))
          {
            String newPackageName = packageName + "." + rest.substring(0, rest.indexOf('/')-1);
		if(!result.contains(newPackageName)) {
              result.addElement(newPackageName);
            }
          }
        }
      }
    }

  }
}
