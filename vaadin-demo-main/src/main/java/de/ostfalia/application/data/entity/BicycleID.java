package de.ostfalia.application.data.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BicycleID implements Serializable {
    private  int channel;
    private LocalDateTime time ;
    public BicycleID(){}

    public BicycleID(int channel, LocalDateTime time){
        this.channel = channel;
        this.time = time;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
