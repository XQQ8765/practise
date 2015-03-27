package barat.test;

import java.util.Map;

public class Test221847 {
	void test() {
		int x = barat.test.testInnerclass.lostclass.value;
	}

	public static void runTest() {
		Map.Entry entryTest;

		barat.Barat.main(new String[] { "barat.test.Test221847" });
	}
}

class testInnerclass {
	public final static class lostclass {
		public static final int value = 5;
	}
}
