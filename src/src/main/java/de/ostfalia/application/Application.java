package de.ostfalia.application;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import de.ostfalia.application.data.fahrrad.impl.DistanceDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

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
    private BikeService bikeService;

    @Autowired
    private DistanceDataProcessor distanceDataProcessor;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void bike(){
/*
        LocalDateTime startTime = LocalDateTime.parse("2023-08-09T16:08:07", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse("2023-08-09T16:08:31", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));


        List<Bicycle> bicycleList = bikeService.getDataWithTimeSpan(1, startTime, endTime);



        for (Bicycle bicycle : bicycleList) {
            System.out.println("Channel: " + bicycle.getChannel() +
                    ", Timestamp: " + bicycle.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                    ", Rotations per second: " + bicycle.getRotations());
        }

 */
        distanceDataProcessor.process();
    }



}
