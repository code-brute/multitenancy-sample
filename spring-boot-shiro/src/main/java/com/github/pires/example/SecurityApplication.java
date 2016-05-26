package com.github.pires.example;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.zama.examples.multitenant.annotation.EnableMultiTenancy;

@EnableAsync // 支持Servlet异步
@SpringBootApplication(scanBasePackages = { "com.github.pires.example"})
@ServletComponentScan
@EnableMultiTenancy
public class SecurityApplication extends SpringBootServletInitializer {

	public static void main(String... args) {
		new SpringApplicationBuilder().sources(SecurityApplication.class).bannerMode(Banner.Mode.OFF).run(args);
	}

}
