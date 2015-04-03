package com.xiaoqq.practise.threadsamplecode.poll;

public class PollingResult {

	private String url;
	private int statusCode;
	private int responseBodyLength;
	private long startTime;
	private long finishTime = System.currentTimeMillis();

	public PollingResult(String url, int statusCode, int responseBodyLength, long startTime) {
		this.url = url;
		this.statusCode = statusCode;
		this.responseBodyLength = responseBodyLength;
		this.startTime = startTime;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public int getResponseBodyLength() {
		return responseBodyLength;
	}

	public String getUrl() {
		return url;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getFinishTime() {
		return finishTime;
	}

	public int getDuration() {
		return (int) (finishTime - startTime);
	}
}
