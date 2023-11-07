package de.ostfalia.application;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import de.ostfalia.application.data.fahrrad.impl.DistanceDataProcessor;
import de.ostfalia.application.data.fahrrad.processing.AbstractDataProcessor;
import de.ostfalia.application.data.service.BikeService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    @Qualifier("speedDataProcessor")
    private AbstractDataProcessor speed;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void bike(){

        LocalDateTime startTime = LocalDateTime.parse("2023-08-09T16:08:07", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse("2023-08-09T16:08:31", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));


        //List<Bicycle> bicycleList = bikeService.getDataWithTimeSpan(1, startTime, endTime);
        speed.process(1, startTime, endTime);

        List<AbstractDataProcessor.ProcessedData> processedDataList = speed.getResults();

        for (AbstractDataProcessor.ProcessedData data : processedDataList) {
            System.out.println("Channel: " + data.getChannel() +
                    ", Timestamp: " + data.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                    ", speed per second: " + data.getValue());
        }



    }



}
