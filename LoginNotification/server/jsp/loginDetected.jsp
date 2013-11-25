<%@page language="java" import="com.dell.loginnotification.DataExchangeWebSocketServlet"%>
<%DataExchangeWebSocketServlet.broadcastLoginNotification(request.getParameter("u"));%>