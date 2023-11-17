package web.ytbcash.wmoney;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WmoneyApplication {
	public static void main(String[] args) {
		SpringApplication.run(WmoneyApplication.class, args);
	}
}
