package de.ostfalia.application.data.service;

import de.ostfalia.application.data.entity.Talsperre;
import de.ostfalia.application.data.entity.Talsperrendaten;
import de.ostfalia.application.data.repository.talsperrendaten.TalsperrendatenRepository;
import de.ostfalia.application.data.repository.talsperre.TalsperreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TalsperrenService {

    private final TalsperreRepository sperrenRepo;

    private final TalsperrendatenRepository datenRepo;

    public TalsperrenService(TalsperreRepository sperrenRepo, TalsperrendatenRepository datenRepo) {
        this.sperrenRepo = sperrenRepo;
        this.datenRepo = datenRepo;
    }

    public List<Talsperre> getAll(){
        return sperrenRepo.findAll();
    }
    public Talsperre getTalsperreById(Long id){
        return sperrenRepo.findById(id).get();
    }
    public List<Talsperrendaten> getDatenByIDLast24H(Long id){
        Pageable pageable = PageRequest.of(0, (4*24));
        return datenRepo.findDataByIdLimit(id, pageable);
    }

    public List<Talsperrendaten> getDatenByIDLast21Days(Long id) {
        Pageable pageable = PageRequest.of(0, (4*24)*21);
        return datenRepo.findDataByIdLimit(id, pageable);

    }

}
