package cl.duoc.portafolio.feriavirtual;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {

	@Value("${server.servlet.context-path}")
	String contextPath;

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("cl.duoc.portafolio.feriavirtual.controller"))
				.paths(PathSelectors.any())

				.build().pathProvider(new RelativePathProvider(null) {
					@Override
					public String getApplicationBasePath() {
						return contextPath;
					}
				}).apiInfo(apiInfo()).securitySchemes(Arrays.asList(apiKey()));

	}

	private ApiKey apiKey() {
		return new ApiKey("jwtToken", "Authorization", "header");
	}

	private ApiInfo apiInfo() {
		return new ApiInfo("FERIA VIRTUAL REST API", "Sistema de Feria Virtual", "API TOS", "Terms of service",
				new Contact("Claudio Hidalgo", "noweb", "cl.hidalgoc@duocuc.cl"), "License of API", "API license URL",
				Collections.emptyList());
	}
}
