package de.ostfalia.application.data.repository.talsperrendaten;

import de.ostfalia.application.data.entity.Talsperre;
import de.ostfalia.application.data.entity.Talsperrendaten;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TalsperrendatenRepository extends JpaRepository<Talsperre, Long> {
    /**
     * jqpl anfrage mit Pageable
     * pageable teilt die Datenmenge in seiten auf
     * Das Pageable objekt wird in dem service erzeugt
     * @param id
     * @param pageable
     * @return
     */
    @Query("select t from Talsperrendaten t where t.fkTalsperre = :fkID order by t.tstamp desc ")
    List<Talsperrendaten> findDataByIdLimit(@Param("fkID") Long id, Pageable pageable);
}
