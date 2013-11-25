package com.dell.loginnotification;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.apache.commons.io.IOUtils;

public class DataExchangeWebSocketServlet extends WebSocketServlet {

public final static Set<ChromeDataExchangeStreamInbound> clients = new HashSet<ChromeDataExchangeStreamInbound>();
private final static Logger logger = Logger.getLogger(DataExchangeWebSocketServlet.class.getName());
protected StreamInbound createWebSocketInbound(String arg0) {
	return new ChromeDataExchangeStreamInbound();
}
protected StreamInbound createWebSocketInbound(String arg0, HttpServletRequest req) {
	return new ChromeDataExchangeStreamInbound();
}

public static void broadcastLoginNotification(String username) throws IOException{
	JSONObject obj = new JSONObject();
	obj.put("title", "User Login Notification");
	obj.put("message", "Detected "+username+" logined!");
	for(ChromeDataExchangeStreamInbound client : clients){
		client.getWsOutbound().writeTextMessage(CharBuffer.wrap(obj.toString()));
	}
	logger.info("broadcast message "+obj.toString()+" to "+clients.size()+" clients");
}

class ChromeDataExchangeStreamInbound extends StreamInbound {

@Override
protected void onBinaryData(InputStream arg0) throws IOException {
}

@Override
protected void onTextData(Reader reader) throws IOException {
	if (!clients.contains(this)) {
		clients.add(this);
	}
	//do nothing
	IOUtils.readLines(reader);
}

@Override
protected void onClose(int status) {
	clients.remove(this);
	super.onClose(status);
}

@Override
protected void onOpen(WsOutbound outbound) {
	clients.add(this);
	super.onOpen(outbound);
}
}
}
