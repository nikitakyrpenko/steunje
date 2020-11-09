package com.negeso.module.webshop;

import com.negeso.module.webshop.interceptor.ApiExceptionResolver;
import com.negeso.module.webshop.interceptor.SecurityInterceptorForApi;
import com.negeso.module.webshop.interceptor.WorkerSecurityInterceptor;
import org.apache.commons.dbcp.BasicDataSource;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableWebMvc
@ComponentScan(basePackages = {
		"com.negeso.module.webshop.controller",
		"com.negeso.module.webshop.service",
		"com.negeso.module.webshop.dao",
		"com.negeso.module.webshop.component",
		"com.negeso.module.webshop.entity",
		"com.negeso.module.webshop.repository",
		"com.negeso.module.webshop.converter"
})
public class ModuleConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private BasicDataSource dataSource;

	@Bean
	public ObjectMapper jsonObjectMapper(){
		return new ObjectMapper();
	}

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new SecurityInterceptorForApi()).addPathPatterns("/webshop/**");
		registry.addInterceptor(new WorkerSecurityInterceptor()).addPathPatterns("/webshop/api/hairdresser/**");
	}

	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/react/");
		resolver.setSuffix(".jsp");
		resolver.setViewClass(JstlView.class);

		return resolver;
	}

	@Bean
	public MultipartResolver multipartResolver(){
		return new CommonsMultipartResolver();
	}

	@Bean
	public HandlerExceptionResolver exceptionResolver(){
		return new ApiExceptionResolver();
	}

	@Bean
	public PlatformTransactionManager transactionManager(){
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean(name = "apiSessionFactory")
	public SessionFactory sessionFactory(){
		return localSessionFactoryBean().getObject();
	}


	@Bean
	public LocalSessionFactoryBean localSessionFactoryBean() {

		AnnotationSessionFactoryBean annotationSessionFactoryBean = new AnnotationSessionFactoryBean();
		annotationSessionFactoryBean.setDataSource(dataSource);
		annotationSessionFactoryBean.setHibernateProperties(hibernateProperties());
		annotationSessionFactoryBean.setPackagesToScan("com.negeso.module.webshop.entity");

		return annotationSessionFactoryBean;
	}

	private final Properties hibernateProperties() {
		Properties hibernateProperties = new Properties();

		hibernateProperties.setProperty(
				"show_sql", "true");
		hibernateProperties.setProperty(
				"hibernate.use_sql_comments", "true");
		hibernateProperties.setProperty(
				"hibernate.hbm2ddl.auto", "update");
		hibernateProperties.setProperty(
				"hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		hibernateProperties.setProperty(
				"hibernate.current_session_context_class", "thread");

		return hibernateProperties;
	}
}
