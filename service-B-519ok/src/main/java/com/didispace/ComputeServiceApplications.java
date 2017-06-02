
package com.didispace;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
//@Configuration
@EnableDiscoveryClient
@SpringBootApplication
//@ComponentScan
public class ComputeServiceApplications {

	public static void main(String[] args) {
		new SpringApplicationBuilder(ComputeServiceApplications.class).web(true).run(args);
	}

}   
