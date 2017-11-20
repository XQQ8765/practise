package com.rabbit.practise.apigateway.consumer;

import com.rabbit.practise.apigateway.service.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {
    @Autowired
    RefactorHelloService refactorHelloService;

    @RequestMapping(value="/hello-feign-consumer", method= RequestMethod.GET)
    public String helloConsumer() {
        StringBuilder sb = new StringBuilder();
        sb.append(refactorHelloService.hello()).append("<br/>");
        sb.append(refactorHelloService.hello("DIDI")).append("<br/>");
        sb.append(refactorHelloService.hello("DIDI", 30)).append("<br/>");
        sb.append(refactorHelloService.hello(new User("DIDI", 40))).append("<br/>");
        return sb.toString();
    }
}
