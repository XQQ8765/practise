package com.rabbit.practise.apigateway.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import com.rabbit.practise.apigateway.service.api.*;

import java.util.Date;

@RestController
public class HelloController implements HelloService {
    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    Environment environment;

    @Override
    public String hello() {
        logServiceInstance("/service/hello");
        String result = "Hello World. date:" + new Date();
        return result;
    }

    @Override
    public String hello(@RequestParam("name") String name) {
        logServiceInstance("/service/hello1");
        return "Hello " + name + ", date:" + new Date();
    }

    @Override
    public User hello(@RequestHeader("name") String name, @RequestHeader("age") Integer age) {
        logServiceInstance("/service/hello2");
        return new User(name, age);
    }

    @Override
    public String hello(@RequestBody User user) {
        logServiceInstance("/service/hello3");
        return "Hello " + user.getName() + "," + user.getAge() + ", date:" + new Date();
    }

    private void logServiceInstance(String url) {
        //https://stackoverflow.com/questions/38916213/how-to-get-the-spring-boot-host-and-port-address-during-run-time
        final String port = environment.getProperty("server.port");
        logger.info(url + ", port:" + port);
    }
}
