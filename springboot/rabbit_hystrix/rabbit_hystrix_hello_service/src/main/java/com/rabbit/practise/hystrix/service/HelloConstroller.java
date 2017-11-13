package com.rabbit.practise.hystrix.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Random;

@RestController
public class HelloConstroller {
    private final Logger logger = Logger.getLogger(getClass());
    @Autowired
    private DiscoveryClient client;

    @RequestMapping(value="/hello", method = RequestMethod.GET)
    public String hello() throws Exception{
        ServiceInstance instance = client.getLocalServiceInstance();

        int sleepTime = new Random().nextInt(3000);
        logger.info("sleepTime:" + sleepTime);
        Thread.sleep(sleepTime);

        logger.info("/hello, host:" + instance.getHost() + ", serviceid:" +instance.getServiceId()
                + ", instance:" + instance + ", sleepTime:" + sleepTime);
        String result = "Hello World. sleepTime: " + sleepTime +", date:" + new Date();
        return result;
    }
}
