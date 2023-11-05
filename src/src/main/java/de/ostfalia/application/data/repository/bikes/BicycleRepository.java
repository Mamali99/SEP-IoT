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


}
