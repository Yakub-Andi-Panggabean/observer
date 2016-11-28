package com.firstwap.dispatcher.observer.configurer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 *
 * @author yakub
 *
 * @createdOn Sep 5, 2016 1:24:00 PM
 *
 */

@Configuration
@ComponentScan({ MvcConfigurer.CONTROLLER_PACKAGE,
	MvcConfigurer.SERVICE_PACKAGE, MvcConfigurer.REPOSITORY_PACKAGE,
		MvcConfigurer.UTIL_PACKAGE })
@PropertySource(value = "classpath:/application.properties")
public class MvcConfigurer extends WebMvcConfigurerAdapter {

	private static final String BASE_PACKAGE = "com.firstwap.dispatcher.observer";
	public static final String CONTROLLER_PACKAGE = BASE_PACKAGE
			+ ".controller";
	public static final String SERVICE_PACKAGE = BASE_PACKAGE + ".service";
	public static final String REPOSITORY_PACKAGE = BASE_PACKAGE
			+ ".repository";
	public static final String UTIL_PACKAGE = BASE_PACKAGE + ".utility";

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/")
				.setViewName("forward:/public/home.html");
	}

	@Override
	public void configureDefaultServletHandling(
			DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("WEB-INF/views/");
		resolver.setSuffix(".jsp");
		return resolver;
	}

}
