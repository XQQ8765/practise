package poll;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class PollingService {

	private CloseableHttpClient httpclient;

	public PollingService(CloseableHttpClient httpclient) {
		this.httpclient = httpclient;
	}

	public PollingResult ping(String url) {
		long startTime = System.currentTimeMillis();
		CloseableHttpResponse response = null;
		try {
			HttpGet get = new HttpGet(url);
			response = httpclient.execute(get, createHttpContext());
			HttpEntity entity = response.getEntity();
			return new PollingResult(url, response.getStatusLine().getStatusCode(), EntityUtils.toByteArray(entity).length, startTime);
		} catch (Throwable e) {
			e.printStackTrace();
			return new PollingResult(url, -1, -1, startTime);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private HttpContext createHttpContext() {
		RequestConfig.Builder builder = RequestConfig.custom().setCookieSpec(CookieSpecs.BEST_MATCH);
		HttpClientContext httpContext = HttpClientContext.create();
		builder.setConnectTimeout(5 * 1000);
		builder.setSocketTimeout(3 * 1000);
		httpContext.setRequestConfig(builder.build());
		httpContext.setCookieStore(new BasicCookieStore());
		return httpContext;
	}

}
