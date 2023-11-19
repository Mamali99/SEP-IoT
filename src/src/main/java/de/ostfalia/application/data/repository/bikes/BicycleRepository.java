package de.ostfalia.application.data.repository.bikes;

import de.ostfalia.application.data.entity.Bicycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface BicycleRepository extends JpaRepository<Bicycle, Long> {

    @Query("Select distinct b.channel from Bicycle b")
    List<Integer> getAllBicycles();
     @Query ("Select b from Bicycle b where b.channel=:id and  b.time between :max and  :min")
    List<Bicycle> getBicycleByChannelAndAndTimeSpan(@Param("id") int id, @Param("min") LocalDateTime min, @Param("max") LocalDateTime max);

     //new Methoden

    //Für den Vergleichsmodus
    @Query("SELECT b FROM Bicycle b WHERE b.channel IN :channels AND b.time BETWEEN :startTime AND :endTime")
    List<Bicycle> findAllBicyclesByChannelsAndTimeSpan(@Param("channels") List<Integer> channels,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime);



     //die Leistungsdaten eines Fahrrads über einen längeren Zeitraum hinweg analysieren
    @Query("SELECT b FROM Bicycle b WHERE b.channel = :channel AND b.time > :sinceTime")
    List<Bicycle> findBicycleDataSince(@Param("channel") int channel, @Param("sinceTime") LocalDateTime sinceTime);


    // Eine Methode, die den letzten Zeitstempel liefert, zu dem ein bestimmter Fahrradkanal Daten gesendet hat.
    @Query("SELECT MAX(b.time) FROM Bicycle b WHERE b.channel = :channel")
    LocalDateTime findLastActivityByChannel(@Param("channel") int channel);


    // alle Kanäle zurückgibt, die mindestens einen Datensatz haben
    @Query("SELECT DISTINCT b.channel FROM Bicycle b WHERE b.time IS NOT NULL")
    List<Integer> getActiveChannels();


}
