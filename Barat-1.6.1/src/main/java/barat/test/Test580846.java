package barat.test;

import barat.OutputVisitor;
import barat.reflect.ArrayInitializer;
import barat.reflect.AExpression;
import barat.reflect.AUserType;
import barat.collections.AExpressionIterator;

public class Test580846 extends OutputVisitor {
    
    int[] a = {1,2};
    int[][] b = {{1,2},{3,4}};
    int[] c = new int[] {5,6};
    int[][] d = new int[][] {{7,8},{8,9}};
    
    public void visitArrayInitializer(ArrayInitializer o) {
	print ("{ ");
	for(AExpressionIterator i=o.getArguments().iterator(); i.hasNext();) {
	    AExpression e = i.next();
	    print ("/*"+e.type().qualifiedName()+"*/");
	    e.accept(this);
	    if (i.hasNext()) print (", ");
	}
	print (" }");
    }
    
    public static void runTest() {
	AUserType t = barat.Barat.getUserType("barat.test.Test580846");
	
	OutputVisitor out = new Test580846();
	t.accept(out);
    }
}

