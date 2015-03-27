package barat.test;

import barat.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

public class RunTests {
  public static void main(String args[]) throws Exception {
    String testPackageName;
    File testDir;
    if(args.length==2) {
      testPackageName = args[0];
      testDir = new File(args[1]);
    } else {
      testPackageName = "barat.test";
      URL runTestURL = RunTests.class.getResource("RunTests.class");
      String runTestString = runTestURL.getFile();
      testDir = new File(runTestString.substring(0, runTestString.length()-14));
    }
    String[] testFileNames = testDir.list();
    List testClasses = new ArrayList();
    for(int i=0; i<testFileNames.length; i++) {
      //System.out.println(testFileNames[i]);
      if(testFileNames[i].startsWith("Test") &&
         testFileNames[i].endsWith(".class") &&
         testFileNames[i].indexOf('$')==-1) {
         testClasses.add(
           java.lang.Class.forName(
             testPackageName
             +"."
             +testFileNames[i].substring(0, testFileNames[i].length()-6)));
      }
    }
    for(Iterator it=testClasses.iterator(); it.hasNext();) {
      java.lang.Class testClass = (java.lang.Class)it.next();
      String testClassName = testClass.getName().substring(11);
      try {
        System.out.print("-------------------- " + testClassName + " -- ");
        Method runTestMethod = testClass.getDeclaredMethod(
                                  "runTest", new java.lang.Class[0]);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
          PrintStream oldOut = System.out;
          PrintStream oldErr = System.err;
          PrintStream newOutAndErr = new PrintStream(bos);
          System.setOut(newOutAndErr);
          System.setErr(newOutAndErr);
          try {
            runTestMethod.invoke(null, new Object[0]);
          } finally {
            System.setOut(oldOut);
            System.setErr(oldErr);
            newOutAndErr.close();
          }
          System.out.println("OK");
        } catch(InvocationTargetException ex) {
          ex.fillInStackTrace();
          Throwable t = ex.getTargetException();
          System.out.println("**** Exception:");
          byte[] testOutput = bos.toByteArray();
          System.out.write(testOutput);
          t.printStackTrace();
        } catch(Exception ex) {
          System.out.println("???? other exception:");
          ex.printStackTrace();
        }
      } catch(NoSuchMethodException ex) {
        System.out.println("#### runTest() method missing");
      } catch(SecurityException ex) {
        System.out.println("???? security exception (!?)");
      }
    }
  }
}
