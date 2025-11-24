package com.miapp.backend.repository;

import com.miapp.backend.model.ElectionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ElectionCategoryRepository extends JpaRepository<ElectionCategory, UUID> {}
