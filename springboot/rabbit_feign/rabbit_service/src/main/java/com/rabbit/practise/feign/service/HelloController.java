package com.rabbit.practise.feign.service;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class HelloController {
    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private DiscoveryClient client;

    @RequestMapping(value="/hello", method= RequestMethod.GET)
    public String hello() {
        List<ServiceInstance> instances = client.getInstances("rabbit-service");
        if (instances != null) {
            for(ServiceInstance instance: instances) {
                logger.info("/hello, host:" + instance.getHost() + ", serviceid:" +instance.getServiceId()
                        + ", instance:" + instance);
            }
        }

        String result = "Hello World. date:" + new Date();
        return result;
    }
}
