package com.xqq.javaclass.example;

public class SwitchExample {
    public final static int TOMAYTO = 0;
		public final static int TOMARTO = 1;

		static void argue() {
			int say = TOMAYTO;
			for (;;) {
				switch (say) {
					case TOMAYTO:
						say = TOMARTO;
						break;
					case TOMARTO:
						say = TOMAYTO;
						break;
				}
			}
		}
}
