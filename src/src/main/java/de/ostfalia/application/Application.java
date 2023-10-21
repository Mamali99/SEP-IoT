package de.ostfalia.application;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import de.ostfalia.application.data.lamp.adapter.Java2NodeRedLampAdapter;
import de.ostfalia.application.data.lamp.lampController.LampController;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@EnableScheduling
@Theme(value = "flowcrmtutorial")
public class Application implements AppShellConfigurator {
    @Autowired
    private LampController lampAdapter;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    /*
    @PostConstruct
    public void testLampMethods() {
        try {
            System.out.println("Switching on the lamp...");
            lampAdapter.switchOn();
            Thread.sleep(5000);  // Wait for 5 seconds
            System.out.println("Switching off the lamp...");
            lampAdapter.switchOff();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

     */

}
