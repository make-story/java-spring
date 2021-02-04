package com.example.demo;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class DemoApplication extends SpringBootServletInitializer {
	//private static final String PROPERTIES = "spring.config.location=classpath:/custom.properties";
	
	public static void main(String[] args) {
		// 시스템 기본 프로퍼티 
		/*Properties p = System.getProperties();  
		Set set = p.entrySet();  
		Iterator itr = set.iterator();  
		while(itr.hasNext()){  
			Map.Entry entry = (Map.Entry)itr.next();  
			System.out.println(entry.getKey()+" = "+entry.getValue());
		}*/
		
		// .properties 주입 방식 run 
		/*new SpringApplicationBuilder(DemoApplication.class) 
		.properties(
				"spring.config.location=" + 
				"classpath:/application.properties" + 
				",classpath:/setting.properties") 
		.run(args);*/
		/*new SpringApplicationBuilder(DemoApplication.class)
		.properties(PROPERTIES)
		.run(args);*/
		
		// 일반 run
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		 return builder.sources(DemoApplication.class);
	}
}