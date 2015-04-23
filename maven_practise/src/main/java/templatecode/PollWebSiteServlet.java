package templatecode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PollWebSiteServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       // saveHash();
	    execute(req, resp);

       // new UUIDThread().start();
        //Submit trace performance data by UUID


       // removeHash();
    }

    private void execute(HttpServletRequest req, HttpServletResponse resp)  throws ServletException, IOException {

    }
    /*
    private void saveHash() {
        String UUID = HashUtil.generateUUID();
        String currentThreadHashId = HashUtil.getCurrentThreadHash();
        HashStorage.put(currentThreadHashId, UUID);
    }

    private void removeHash() {
        String currentThreadHashId = HashUtil.getCurrentThreadHash();
        String UUID = HashStorage.getByObjectHashId(currentThreadHashId);
        System.out.println("Servlet: Thread Hash:" + currentThreadHashId + ", UUID:" + UUID);
        HashStorage.remove(currentThreadHashId);
    }   */
}
