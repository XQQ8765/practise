package com.rabbit.practise.feign.service;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class HelloController {
    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    Environment environment;

    @Autowired
    private DiscoveryClient client;

    @RequestMapping(value="/hello", method= RequestMethod.GET)
    public String hello() {
        logServiceInstance("/hello");
        String result = "Hello World. date:" + new Date();
        return result;
    }

    @RequestMapping(value="/hello1", method= RequestMethod.GET)
    public String hello1(@RequestParam String name) {
        logServiceInstance("/hello1");
        return "Hello " + name + ", date:" + new Date();
    }

    @RequestMapping(value="/hello2", method= RequestMethod.GET)
    public User hello2(@RequestHeader String name, @RequestHeader Integer age) {
        logServiceInstance("/hello2");
        return new User(name, age);
    }

    @RequestMapping(value="/hello3", method= RequestMethod.POST)
    public String hello3(@RequestBody User user) {
        logServiceInstance("/hello3");
        return "Hello " + user.getName() + "," + user.getAge() + ", date:" + new Date();
    }

    private void logServiceInstance(String url) {
        List<ServiceInstance> instances = client.getInstances("rabbit-service");
        //https://stackoverflow.com/questions/38916213/how-to-get-the-spring-boot-host-and-port-address-during-run-time
        final String port = environment.getProperty("server.port");
        logger.info(url + ", port:" + port);
    }
}
