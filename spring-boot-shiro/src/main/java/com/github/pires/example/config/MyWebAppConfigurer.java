package com.github.pires.example.config;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import com.github.pires.example.interceptor.JsonErrorMsgInterceptor;

@Configuration
public class MyWebAppConfigurer extends WebMvcConfigurerAdapter implements EnvironmentAware {

	private static final Logger logger = LoggerFactory.getLogger(MyWebAppConfigurer.class);

	private RelaxedPropertyResolver propertyResolver;

	@Value("${spring.datasource.url}")
	private String myUrl;

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// registry.addViewController("/").setViewName("forward:/index");
		// registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		super.addViewControllers(registry);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 多个拦截器组成一个拦截器链
		// addPathPatterns 用于添加拦截规则
		// excludePathPatterns 用户排除拦截
		// registry.addInterceptor(new MyInterceptor1()).addPathPatterns("/**");
		// registry.addInterceptor(new MyInterceptor2()).addPathPatterns("/**");
		// registry.addInterceptor(new
		// JsonErrorMsgInterceptor()).addPathPatterns("*.json");
		registry.addInterceptor(new JsonErrorMsgInterceptor()).addPathPatterns("/**");
		super.addInterceptors(registry);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 访问myres根目录下的fengjing.jpg 的URL为
		// http://localhost:8080/myres/fengjing.jpg 不影响Spring Boot的默认的 /**
		// 映射，可以同时使用。
		registry.addResourceHandler("/myres/**").addResourceLocations("classpath:/myres/");
		// 访问myres根目录下的fengjing.jpg 的URL为 http://localhost:8080/fengjing.jpg
		// （/** 会覆盖系统默认的配置）
		// registry.addResourceHandler("/**").addResourceLocations("classpath:/myres/").addResourceLocations("classpath:/static/");

		// 可以直接使用addResourceLocations 指定磁盘绝对路径，同样可以配置多个位置，注意路径写法需要加上file:
		registry.addResourceHandler("/myimgs/**").addResourceLocations("file:d:/myimgs/");
		super.addResourceHandlers(registry);
	}

	/**
	 * 这个方法只是测试实现EnvironmentAware接口，读取环境变量的方法。
	 */
	@Override
	public void setEnvironment(Environment env) {
		logger.info(env.getProperty("JAVA_HOME"));
		logger.info(myUrl);
		String str = env.getProperty("spring.datasource.url");
		logger.info(str);
		propertyResolver = new RelaxedPropertyResolver(env, "spring.datasource.");
		String url = propertyResolver.getProperty("url");
		logger.info(url);
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		for (HttpMessageConverter<?> httpMessageConverter : converters) {
			// 为 MappingJackson2HttpMessageConverter 添加 "application/javascript"
			// 支持，用于响应JSONP的Content-Type
			if (httpMessageConverter instanceof MappingJackson2HttpMessageConverter) {
				MappingJackson2HttpMessageConverter convert = (MappingJackson2HttpMessageConverter) httpMessageConverter;
				List<MediaType> medisTypeList = new ArrayList<>(convert.getSupportedMediaTypes());
				medisTypeList.add(MediaType.valueOf("application/javascript;charset=UTF-8"));
				convert.setSupportedMediaTypes(medisTypeList);
				break;
			}
		}
		super.extendMessageConverters(converters);
	}

	// @Override
	// public void configureContentNegotiation(ContentNegotiationConfigurer
	// configurer) {
	// configurer.defaultContentType(MediaType.TEXT_HTML)
	// // .useJaf(true)
	// // .favorPathExtension(true)
	// .mediaType("xml", MediaType.APPLICATION_XML).mediaType("pdf",
	// MediaType.valueOf("application/pdf")).mediaType("json",
	// MediaType.APPLICATION_JSON)
	// .mediaType("xls",
	// MediaType.valueOf("application/vnd.ms-excel")).ignoreAcceptHeader(true);
	// }

	/*
	 * Configure ContentNegotiatingViewResolver
	 */
	@Bean
	public ViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager) {
		ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
		resolver.setContentNegotiationManager(manager);

		// Define all possible view resolvers
		List<ViewResolver> resolvers = new ArrayList<ViewResolver>();

		// resolvers.add(new JsonViewResolver());
		// resolvers.add(new PdfViewResolver());
		// resolvers.add(new XlsViewResolver());

		resolver.setViewResolvers(resolvers);
		return resolver;
	}

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
		return processor;
	}
}
