package com.rabbit.practise.feign.service;

import com.rabbit.practise.feign.service.api.HelloService;
import com.rabbit.practise.feign.service.api.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class RefactorHelloController implements HelloService {
    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    Environment environment;

    @Override
    public String hello() {
        logServiceInstance("/refactor/hello");
        String result = "Hello World. date:" + new Date();
        return result;
    }

    @Override
    public String hello(@RequestParam("name") String name) {
        logServiceInstance("/refactor/hello4");
        return "Hello " + name + ", date:" + new Date();
    }

    @Override
    public User hello(@RequestHeader("name") String name, @RequestHeader("age") Integer age) {
        logServiceInstance("/refactor/hello5");
        return new User(name, age);
    }

    @Override
    public String hello(@RequestBody User user) {
        logServiceInstance("/refactor/hello6");
        return "Hello " + user.getName() + "," + user.getAge() + ", date:" + new Date();
    }


    private void logServiceInstance(String url) {
        //https://stackoverflow.com/questions/38916213/how-to-get-the-spring-boot-host-and-port-address-during-run-time
        final String port = environment.getProperty("server.port");
        logger.info(url + ", port:" + port);
    }
}
