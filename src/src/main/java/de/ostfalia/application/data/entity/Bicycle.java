package de.ostfalia.application.data.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;



@Entity
@Table(name = "bicycle")
@IdClass(BicycleID.class)
public class Bicycle implements Comparable<Bicycle>{

    @Column(name= "channel")
    @Id
    int channel;

    @Column(name = "rotations_per_second")
    BigDecimal rotations;
    @Column(name = "timestamp", columnDefinition = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    @Id
    LocalDateTime time ;



    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public BigDecimal getRotations() {
        return rotations;
    }

    public void setRotations(BigDecimal rotations) {
        this.rotations = rotations;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "zeit" + time + " channel " + channel + " rotations " + rotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bicycle bicycle = (Bicycle) o;
        if (channel != bicycle.channel) return false;
        return time.equals(bicycle.time);
    }

    @Override
    public int hashCode() {
        int result = channel;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo( Bicycle o) {

        LocalTime time1 = this.getTime().toLocalTime();
        LocalTime time2 = o.getTime().toLocalTime();
        return channel - o.getChannel() + time1.compareTo(time2);
    }
}
