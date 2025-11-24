package com.miapp.backend.repository;

import com.miapp.backend.model.PoliticalParty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PoliticalPartyRepository extends JpaRepository<PoliticalParty, UUID> {

}
