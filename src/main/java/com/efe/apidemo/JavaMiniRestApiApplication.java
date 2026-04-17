package com.efe.apidemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // bu etiket Spring'e der ki “Başlangıç noktası burası. Bu paketin altındaki sınıfları tara ve uygulamayı ayağa kaldır.”
public class JavaMiniRestApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaMiniRestApiApplication.class, args);
	}

}
