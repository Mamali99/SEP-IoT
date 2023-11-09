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
        return bicycleRepository.getBicycleByChannelAndAndTimeSpan(id, max, min);
    }

    public List<Integer> getAvailableChannels() {
        return bicycleRepository.getActiveChannels();
    }

    public List<Bicycle> getBicyclesForLastDuration(int channel, Duration duration) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minus(duration);
        return bicycleRepository.getBicycleByChannelAndAndTimeSpan(channel, startTime, endTime);
    }

    public List<Bicycle> getBicyclesSinceLastActivity(int channel) {
        LocalDateTime lastActivityTime = bicycleRepository.findLastActivityByChannel(channel);
        if (lastActivityTime != null) {
            // Sie könnten hier eine Toleranz hinzufügen, z.B. lastActivityTime.minusMinutes(1)
            return bicycleRepository.findBicycleDataSince(channel, lastActivityTime);
        }
        return new ArrayList<>();
    }


    public List<Bicycle> findBicycleDataSince(int channel, LocalDateTime sinceTime) {
        return bicycleRepository.findBicycleDataSince(channel, sinceTime);
    }

    public LocalDateTime findLastActivityByChannel(int channel) {
        return bicycleRepository.findLastActivityByChannel(channel);
    }
}
