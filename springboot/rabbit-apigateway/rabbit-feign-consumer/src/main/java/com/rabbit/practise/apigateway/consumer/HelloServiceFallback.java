package com.rabbit.practise.apigateway.consumer;


import com.rabbit.practise.apigateway.service.api.HelloService;
import com.rabbit.practise.apigateway.service.api.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
//Add another RequestMapping "/fallback" to avoid "BeanCreationException" due to conflict within fallback method and bean method.
@RequestMapping(value = "/fallback")
public class HelloServiceFallback implements RefactorHelloService {
    @Override
    public String hello() {
        return "error reported in HelloServiceFallback.";
    }

    @Override
    public String hello(@RequestParam("name") String name) {
        return "error reported in HelloServiceFallback.";
    }

    @Override
    public User hello(@RequestHeader("name") String name, @RequestHeader("age") Integer age) {
        return new User("unknown", 0);
    }

    @Override
    public String hello(@RequestBody User user) {
        return "error reported in HelloServiceFallback.";
    }

}
