package de.ostfalia.application.data.service;

import de.ostfalia.application.data.entity.Bicycle;
import de.ostfalia.application.data.repository.bikes.BicycleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BikeService {

    private final BicycleRepository bicycleRepository;

    public BikeService(BicycleRepository bicycleRepository) {
        this.bicycleRepository = bicycleRepository;
    }

    public List<Bicycle> getDataWithTimeSpan(int id, LocalDateTime min, LocalDateTime max){
        List<Bicycle> bicycles = bicycleRepository.getBicycleByChannelAndAndTimeSpan(id, max, min);
        for(Bicycle b: bicycles)
            System.out.println("Channel: "+b.getChannel()+" Time: "+b.getTime()+ " Rotation: " + b.getRotations() );
        return bicycles;
    }

    public List<Integer> getAvailableChannels() {
        return bicycleRepository.getActiveChannels();
    }


    public List<Bicycle> getBicyclesSinceLastActivity(int channel) {
        LocalDateTime lastActivityTime = bicycleRepository.findLastActivityByChannel(channel);
        if (lastActivityTime != null) {
            return bicycleRepository.findBicycleDataSinceLastActivity(channel, lastActivityTime, LocalDateTime.now());
        }
        return new ArrayList<>();
    }



}
