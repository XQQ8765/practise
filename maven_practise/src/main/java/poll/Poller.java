package poll;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Poller {

	private static CloseableHttpClient httpclient = null;
	static {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(10);
		cm.setDefaultMaxPerRoute(2);
		httpclient = HttpClients.custom().setConnectionManager(cm).build();
	}

	private static ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(10);

	public List<PollingResult> poll(List<String> urls) {
		final Collection<Callable<PollingResult>> tasks = new ArrayList<Callable<PollingResult>>();
		for (final String url : urls) {
			tasks.add(new Callable<PollingResult>() {
				@Override
				public PollingResult call() throws Exception {
					return new PollingService(httpclient).ping(url);
				}
			});
		}
		List<PollingResult> results = new ArrayList<PollingResult>();
		try {
			for (Future<PollingResult> task : executor.invokeAll(tasks)) {
				results.add(task.get());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}



}