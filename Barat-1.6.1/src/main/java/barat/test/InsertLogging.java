package barat.test;

import barat.reflect.*;
import barat.*;

public class InsertLogging extends OutputVisitor
{
  public static void main(String[] args)
  {
    InsertLogging v = new InsertLogging();
    for(int i=0; i<args.length; i++)
    {
      CompilationUnit cu = (CompilationUnit)Barat.getUserType(args[i]).container();
      cu.accept(v);
    }
  }

  public void visitBlock(Block o)
  {
    if(o.container() instanceof ConcreteMethod)
    {
      String methodName = ((ConcreteMethod)o.container()).qualifiedName();
      println("{");
      currentIndent++;
      indent(); println("log.Log.enterMethod(\"" + methodName + "\");");
      indent(); print("try ");
      super.visitBlock(o); nl();
      indent(); println("finally {");
      indent(); println("\tlog.Log.exitMethod(\"" + methodName + "\");");
      indent(); println("}");
      currentIndent--;
      indent(); println("}");
    }
    else
    {
      super.visitBlock(o);
    }
  }
}
