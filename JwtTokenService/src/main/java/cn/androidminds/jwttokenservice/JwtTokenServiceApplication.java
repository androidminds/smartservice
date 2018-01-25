package cn.androidminds.jwttokenservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication(scanBasePackages="cn.androidminds")
@EnableDiscoveryClient
@EnableFeignClients
public class JwtTokenServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(JwtTokenServiceApplication.class, args);
	}
}
