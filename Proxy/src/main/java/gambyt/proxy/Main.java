package gambyt.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import gambyt.proxy.controllers.RMIInstance;

@SpringBootApplication
public class Main {
	public static void main(String[] args) {
		RMIInstance.initRMI();
		SpringApplication.run(Main.class, args);
	}
}
