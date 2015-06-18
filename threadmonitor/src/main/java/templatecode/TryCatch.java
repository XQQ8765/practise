package templatecode;

/**
 * Created by rxiao on 6/18/15.
 */
public class TryCatch {

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

    private void callSomeMethod() {

    }

    private void handleException(RuntimeException e) {

    }

    private void callFinallyMethod() {

    }
}
