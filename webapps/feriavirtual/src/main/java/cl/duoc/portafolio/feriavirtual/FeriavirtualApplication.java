package cl.duoc.portafolio.feriavirtual;

import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@SpringBootApplication(scanBasePackages = { "cl.duoc.portafolio.feriavirtual" })
@EnableScheduling
@EnableAsync
public class FeriavirtualApplication {

	@Autowired
	private Jackson2ObjectMapperBuilder builder;
	
	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

		this.builder.serializers(new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE));

		this.builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

		this.builder.featuresToEnable(SerializationFeature.INDENT_OUTPUT);

		this.builder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

		this.builder.featuresToEnable(SerializationFeature.INDENT_OUTPUT);

		this.builder.featuresToEnable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	}
	
	@Bean
	public FilterRegistrationBean<CorsFilter> filterRegistrationBean() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}
	
	
	@Bean
	public TaskScheduler getTaskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(7);
		scheduler.setThreadNamePrefix("feria_scheduler-");
		scheduler.initialize();

		return scheduler;
	}

	@Bean
	public TaskExecutor getTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(7);
		executor.setThreadNamePrefix("feria_executor-");
		executor.initialize();

		return executor;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(FeriavirtualApplication.class, args);
	}

}
