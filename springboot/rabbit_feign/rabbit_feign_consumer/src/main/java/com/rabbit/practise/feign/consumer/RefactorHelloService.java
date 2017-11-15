package com.rabbit.practise.feign.consumer;
import com.rabbit.practise.feign.service.api.HelloService;
import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient("rabbit-service")
public interface RefactorHelloService extends HelloService {

}
