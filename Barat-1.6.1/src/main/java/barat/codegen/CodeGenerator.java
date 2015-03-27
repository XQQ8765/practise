package barat.codegen;

import barat.*;
import barat.collections.*;
import barat.reflect.*;
import barat.parser.*;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.Constants;
import java.io.*;
import java.util.*;

/**
 * Generate code for a compilation unit, i.e. a file.
 *
 * @version $Id: CodeGenerator.java,v 1.3 2002/01/28 10:48:35 dahm Exp $
 * @author  <A HREF="http://www.inf.fu-berlin.de/~dahm">M. Dahm</A>
 */
public class CodeGenerator implements Constants {
  static boolean  debug;
  static BitSet   optimize = new BitSet();

  private static ToDoList todo_list = new ToDoList();
  private static String   output;

  private CompilationUnit cu;
  private Set             finalizers = new TreeSet();

  String        source_path, source_file;
  ClassMap      class_map = new ClassMap(); // Map for all classes in this unit

  static {
    for(int i=0; i < 32; i++) // Turn on any optimization
      optimize.set(i);
  }

  /** This Factory method may be overridden by sub classes
   */
  public ClassGenerator createClassGenerator() {
    return new ClassGenerator(this);
  }

  /** This Factory method may be overridden by sub classes
   */
  public InnerClassGenerator createInnerClassGenerator() {
    return new InnerClassGenerator(this);
  }

  /** Run this piece of code just before the class finally gets dumped.
   */
  public void addFinalizer(CodeSuspension code) {
    finalizers.add(code);
  }

  //
  //*************** Top-level ***************
  //

  public CodeGenerator(CompilationUnit cu) {
    this.cu = cu;
    source_path = cu.filename();
    //System.out.println("***** Compiling file " + source_path);
    int index = source_path.lastIndexOf(File.separatorChar);
    source_path = source_path.substring(0, index + 1);
    source_file = cu.filename().substring(index + 1);

  }

  public void start() {
    for(InterfaceIterator i=cu.getInterfaces().iterator(); i.hasNext();) {
      Interface inter = i.next();
      inter.accept(new InnerClassVisitor());
      inter.accept(createClassGenerator());
    }

    for(ClassIterator i=cu.getClasses().iterator(); i.hasNext();) {
       barat.reflect.Class clazz = i.next();
       clazz.accept(new InnerClassVisitor());
       clazz.accept(createClassGenerator());
    }

    // Check and dump all classes
    for(Stack s = class_map.elements(); !s.empty();) {
      ClassGenerator cg = (ClassGenerator)s.pop();
      cg.check();
      dumpClass(cg);
    }
  }

  /**
   * Dump class to file.
   */
  private void dumpClass(ClassGenerator cg) {
    for(java.util.Iterator i = finalizers.iterator(); i.hasNext(); )
      ((CodeSuspension)i.next()).execute(cg, cg.cl);

    ClassGen clazz      = cg.getClassGenerator();
    String   class_name = clazz.getClassName();

    try {
      int index = class_name.lastIndexOf('.');
      String name;
      
      if(output == null) {
	name = source_path +
	  ((index > 0)? class_name.substring(index + 1) : class_name) + ".class";
      } else {
	name = output + File.separatorChar +
	  class_name.replace('.', File.separatorChar) + ".class";
      }

      File file = new File(name);
      File dir  = new File(file.getParent());
      dir.mkdirs();

      clazz.getJavaClass().dump(file);
      
      System.out.println("****  Class " + class_name + " dumped to file " + file.getName() + ".");
    } catch(IOException e) { System.err.println(e); }
  }

  private static class ToDoList {
    private Stack stack = new Stack();

    final void add(CompilationUnit cu) { stack.push(cu); }
    final CompilationUnit get() {
      return (stack.size() > 0)? (CompilationUnit)stack.pop():null;
    }
  }

  public static void main(String[] args) { // args[] are fully qualified class names 
    boolean debug = false;

    Factory.addAnalysis(new ConstantAnalysis());
    Barat.preferByteCode = false;
    Barat.debugLoading   = false;

    for(int i=0; i < args.length; i++) {
      if(args[i].startsWith("-") || args[i].startsWith("+")) {
	if(args[i].equals("-debug"))
	  debug = true;
	else if(args[i].equals("-noopt"))
	  optimize = null;
	else if(args[i].startsWith("-O")) {
	  optimize = new BitSet();
	  optimize.set(Integer.parseInt(args[i].substring(2)));
	} else if(args[i].startsWith("+O"))
	  optimize.clear(Integer.parseInt(args[i].substring(2)));
	else if(args[i].startsWith("-o="))
	  output = args[i].substring(3);
	else
	  System.err.println("Unknown option " + args[i] + " ignored.");
      }
      else {
	AUserType       t  = barat.Barat.getUserType(args[i]); // Class or interface
	CompilationUnit cu = (CompilationUnit)t.containing(CompilationUnit.class);
	todo_list.add(cu);
      }
    }

    Barat.preferByteCode   = true;
    CodeGenerator.debug    = debug;

    NameAnalysis.addSourceParsedObserver(new SourceParsedObserver() {
      public void sourceParsed(CompilationUnit cu) {
	CodeGenerator.todo_list.add(cu);
      }
    });

    for(CompilationUnit cu = todo_list.get(); cu != null; cu = todo_list.get())
      new CodeGenerator(cu).start();
  }

  static String getLocation(barat.Node n) {
    CompilationUnit cu   = (CompilationUnit)n.containing(CompilationUnit.class);
    StringBuffer    buf  = new StringBuffer(cu.filename() + ":");
    int             line = n.line_number();

    if(line != -1)
      buf.append(line + ":");

    return buf.toString();
  }

  //
  //*************** Debugging ***************
  //

  static {
    String s = System.getProperty("barat.debug");
    debug = (s != null);
  }

  static void error(String mesg) {
    System.err.println(mesg);
    System.exit(-1);
  }

  static final void do_assert(boolean assertion, String mesg) {
    if(!assertion)
      throw new RuntimeException("Assertion failed: " + mesg);
  }

  public String getSourcePath() { return source_path; }
  public String getSourceFile() { return source_file; }

  public static String implementationNameOf(ANamed n) {
    if(n instanceof AUserType) {
      AUserType a = (AUserType)n.containing(AUserType.class);

      if(a != null) { // Fix for BB, what about inner inner classes!?
	String s     = implementationNameOf(a);
	String name  = n.getName();  // For anonymous classes name is already A$1
	int    index = name.lastIndexOf('$');

	return s + "$" + name.substring(index + 1);
      }
    }
    
    ANamed c = (ANamed)n.containing(barat.reflect.ANamed.class);
    if(c != null)
      return implementationNameOf(c) + "." + n.getName();

    barat.reflect.Package p = (barat.reflect.Package)n.containing(barat.reflect.Package.class);
    if(p == null)
      return n.getName();

    String pname = p.getQualifiedName().toString();
    if(pname.length() != 0)
      pname = pname + ".";

    return pname + n.getName();
  }
}
