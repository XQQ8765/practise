package com.rabbit.practise.apigateway.consumer;

import com.rabbit.practise.apigateway.service.api.HelloService;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(name="rabbit-service", configuration = FullLogConfiguration.class, fallback = HelloServiceFallback.class)
public interface RefactorHelloService extends HelloService {

}
