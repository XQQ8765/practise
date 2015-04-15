package com.dell.sample.instrumentation.asm;

import org.objectweb.asm.Opcodes;

/**
 * Created by rxiao on 4/2/15.
 */
public interface IConstant {
    int ASM_API = Opcodes.ASM4;

    String CLASS_NAME_POLL_WEB_SITE_SERVLET = "com.dell.apm.target.monitor.app.ping.PollWebSiteServlet";
    String CLASS_NAME_POLLER = "com.dell.apm.target.monitor.app.ping.Poller";
    String CLASS_NAME_POLLING_SERVICE = "com.dell.apm.target.monitor.app.ping.PollingService";
    //String CLASS_NAME_POLLER = "com.xiaoqq.practise.threadsamplecode.poll.Poller";
}
