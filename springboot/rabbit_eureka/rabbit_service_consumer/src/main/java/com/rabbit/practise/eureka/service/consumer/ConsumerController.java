package com.rabbit.practise.eureka.service.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumerController {
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/ribbon-consumer", method= RequestMethod.GET)
    public String helloConsumer() {
        String serviceResult = restTemplate.getForEntity("http://rabbit-eureka-hello-service/hello", String.class)
                .getBody();
        return "ConsumerController: hello-service result ---> " + serviceResult;
    }
}
