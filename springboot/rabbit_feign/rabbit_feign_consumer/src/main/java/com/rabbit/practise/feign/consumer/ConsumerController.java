package com.rabbit.practise.feign.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {
    @Autowired
    HelloService helloService;

    @Autowired
    RefactorHelloService refactorHelloService;

    @RequestMapping(value="/feign-consumer", method= RequestMethod.GET)
    public String helloConsumer() {
        return helloService.hello();
    }

    @RequestMapping(value="/feign-consumer2", method= RequestMethod.GET)
    public String helloConsumer2() {
        StringBuilder sb = new StringBuilder();
        sb.append(helloService.hello()).append("<br/>");
        sb.append(helloService.hello("DIDI")).append("<br/>");
        sb.append(helloService.hello("DIDI", 30)).append("<br/>");
        sb.append(helloService.hello(new User("DIDI", 40))).append("<br/>");
        return sb.toString();
    }

    @RequestMapping(value="/feign-consumer3", method= RequestMethod.GET)
    public String helloConsumer3() {
        StringBuilder sb = new StringBuilder();
        sb.append(refactorHelloService.hello()).append("<br/>");
        sb.append(refactorHelloService.hello("refactor-DIDI")).append("<br/>");
        sb.append(refactorHelloService.hello("refactor-DIDI", 30)).append("<br/>");
        sb.append(refactorHelloService.hello(new com.rabbit.practise.feign.service.api.User("refactor-DIDI", 40))).append("<br/>");
        return sb.toString();
    }
}
