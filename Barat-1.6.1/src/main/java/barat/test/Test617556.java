package barat.test;

import barat.OutputVisitor;
import barat.reflect.AUserType;

public class Test617556 extends OutputVisitor {
        
    public static void runTest() {
	AUserType t = barat.Barat.getUserType("barat.test.AssertTest");
	
	OutputVisitor out = new OutputVisitor();
	t.accept(out);
    }
    
    public static void main(String[] args) {
    	runTest();
    }
}

