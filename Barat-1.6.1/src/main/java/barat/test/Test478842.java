package barat.test;

class Parent {
	String m() {
		return "Parent";
	}
}

public class Test478842 extends Parent {
	String m() {
		return "Test478842";
	}

	class Inner {
		String m() {
			return "Inner";
		}

		String test() {
			return Test478842.super.m();
		}
	}

	public static void runTest() throws Exception {
		Test478842 b = new Test478842();
		Inner i = b.new Inner();
		System.out.println(i.test());
		java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
		barat.OutputVisitor v = new barat.OutputVisitor(bos);
		barat.Barat.getUserType("barat.test.Test478842").accept(v);
		bos.close();
		String result = new String(bos.toByteArray());
		if(result.indexOf("Test478842.super.m")==-1) {
		    throw new RuntimeException("output does not contain the super call:\n"+result);
		}
		if(result.indexOf("err"+"or")!=-1) {
		    throw new RuntimeException("output contains an err"+"or:\n"+result);
		}
	}
}
