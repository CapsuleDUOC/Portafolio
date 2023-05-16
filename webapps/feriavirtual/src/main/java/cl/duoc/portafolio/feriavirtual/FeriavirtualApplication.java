package cl.duoc.portafolio.feriavirtual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = { "cl.duoc.portafolio.feriavirtual" })
@EnableScheduling
@EnableAsync
public class FeriavirtualApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeriavirtualApplication.class, args);
	}

}
