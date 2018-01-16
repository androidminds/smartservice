package cn.androidminds.jwtservice;

import cn.androidminds.jwtservice.service.AuthFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
//@ServletComponentScan
public class JwtServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtServiceApplication.class, args);
	}

/*
	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		AuthFilter httpBasicFilter = new AuthFilter();
		registrationBean.setFilter(httpBasicFilter);
		List<String> urlPatterns = new ArrayList<String>();
		urlPatterns.add("/auth/refresh");
		registrationBean.setUrlPatterns(urlPatterns);
		return registrationBean;
	}*/

}
