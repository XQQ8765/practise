package com.xqq.javaclass.example;

/**
 * Created by rxiao on 4/14/15.
 */
public class FinallyExample {

    static int givemeThatOldFashionedBoolean(boolean bVal) {
        try {
            if (bVal) {
                return 1;
            }
            return 0;
        }
        finally {
            System.out.println("Got old fashioned.");
        }
    }

    static boolean superiseTheProgrammer(boolean bVal) {
        while (bVal) {
            try {
                return true;
            }
            finally {
                break;
            }
        }
        return false;
    }

    public void simpleTryCatch() {
        try {
            callSomeMethod();
        } catch (RuntimeException e) {
            handleException(e);
        }
    }

    public void tryCatchFinally(boolean arg) {
        try {
            callSomeMethod();
            if (arg) {
                return;
            }
            callSomeMethod();
        } catch (RuntimeException e) {
            handleException(e);
        } catch (Exception e) {
            return;
        } finally {
            callFinallyMethod();
        }
    }

    private void callFinallyMethod() {
    }

    private void callSomeMethod() {
    }

    private void handleException(Exception e) {
    }

    public static void main(String[] args) {
        boolean bValue = true;
        boolean v = superiseTheProgrammer(bValue);
        System.out.println("v="+v);//output: v=false
    }
}
