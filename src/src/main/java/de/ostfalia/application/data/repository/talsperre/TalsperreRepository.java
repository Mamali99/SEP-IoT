package de.ostfalia.application.data.repository.talsperre;

import de.ostfalia.application.data.entity.Talsperre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface TalsperreRepository extends JpaRepository<Talsperre, Long> {
    // es muss "Talsperren" heissen da der name in @Entity auf "Talsperren" gesetzt wurde
    @Query("select t from Talsperre t " +
            "where t.name like :searchTerm")
    List<Talsperre> findByName(@Param("searchTerm") String searchTerm);// param muss genauso heissen wie in der query oben

    @Query("select t from Talsperre t") // jqpl anfrage
    List<Talsperre> findAll();
    // Optional<Talsperre> kann wert enthalten, muss aber nicht
    // siehe interface CrudRepository, welche in der Vererbungshierarchie mit implementiert ist
    @Query("select t from Talsperre t where t.id = :id")
    Optional<Talsperre> findById(@Param("id") Long id);
}
