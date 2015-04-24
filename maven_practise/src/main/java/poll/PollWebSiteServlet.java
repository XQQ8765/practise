package poll;

import net.sf.json.JSONSerializer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PollWebSiteServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2606194110525479673L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> urls = new ArrayList<String>();
        urls.add("http://www.163.com");
        urls.add("http://www.qq.com");
        urls.add("http://torbugs.prod.quest.corp/secure/Dashboard.jspa");

        Poller poller = new Poller();
        List<PollingResult> results = poller.poll(urls);
        System.out.println("results:" + results);
	}

    public static void main(String [] args) throws ServletException, IOException {
        new PollWebSiteServlet().doPost(null, null);
    }

}
