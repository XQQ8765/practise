package com.rabbit.practise.feign.consumer;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("rabbit-service")
public interface HelloService {

    @RequestMapping("/hello")
    String hello();
}
