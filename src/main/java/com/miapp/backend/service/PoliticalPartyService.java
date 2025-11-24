package com.miapp.backend.service;

import com.miapp.backend.model.PoliticalParty;
import com.miapp.backend.repository.PoliticalPartyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PoliticalPartyService {

    private final PoliticalPartyRepository repo;

    public PoliticalPartyService(PoliticalPartyRepository repo) {
        this.repo = repo;
    }

    public List<PoliticalParty> getAll() {
        return repo.findAll();
    }

    public PoliticalParty create(PoliticalParty party) {
        return repo.save(party);
    }

    public PoliticalParty update(UUID id, PoliticalParty party) {
        PoliticalParty db = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        db.setName(party.getName());
        db.setLogoPath(party.getLogoPath());

        return repo.save(db);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
