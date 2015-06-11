package templatecode;

/**
 * Created by rxiao on 6/11/15.
 */
public class Wait {

    public Object beforeWait() {
        return this;
    }
    private static void invokeStaticMethod() {
        Object a = new Object();
        String b = "b";
        testStaticMethod(a, b);
    }
    private static void testStaticMethod(Object a, String b) {

    }


    public CodePosition createCodePosition() {
        int i = 0;
        i = 2;
        CodePosition codePosition = new CodePosition();
        codePosition.setClassName("XXX_className");
        codePosition.setMethodName("XXX_methodName");
        codePosition.setMethodDesc("XXX_methodDesc");

        //EventListener.beforeWait(i, codePosition);
        return codePosition;
    }
    public CodePosition create() {
        int i = 0;
        i = 2;
        CodePosition codePosition = new CodePosition();
        return codePosition;
    }

    public static CodePosition staticCreate() {
        int i = 0;
        i = 2;
        CodePosition codePosition = new CodePosition();
        return codePosition;
    }
    public Object returnThis() {
        int i = 0;
        i = 2;
        Object obj = this;
        return obj;
    }
    public int test() {
        int i = 0;
        i = 2;
        return i;
    }

    public int test2() {
        int i = 0;
        String t = "str";
        i = 2;
        return i;
    }



    static class CodePosition {
        private String className;
        private String methodName;
        private String methodDesc;

        public CodePosition() {

        }
        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getMethodDesc() {
            return methodDesc;
        }

        public void setMethodDesc(String methodDesc) {
            this.methodDesc = methodDesc;
        }
    }
}
