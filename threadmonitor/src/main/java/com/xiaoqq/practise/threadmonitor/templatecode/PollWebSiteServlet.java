package com.xiaoqq.practise.threadmonitor.templatecode;

import com.xiaoqq.practise.threadmonitor.uuid.HashStorage;
import com.xiaoqq.practise.threadmonitor.uuid.HashUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PollWebSiteServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        saveHash();
	    execute(req, resp);
        removeHash();
    }

    private void execute(HttpServletRequest req, HttpServletResponse resp)  throws ServletException, IOException {

    }

    private void saveHash() {
        String UUID = HashUtil.generateUUID();
        String objectHashId = HashUtil.getCurrentThreadHash();
        HashStorage.put(objectHashId, UUID);
    }

    private void removeHash() {
        String objectHashId = HashUtil.getCurrentThreadHash();
        HashStorage.remove(objectHashId);
    }
}
