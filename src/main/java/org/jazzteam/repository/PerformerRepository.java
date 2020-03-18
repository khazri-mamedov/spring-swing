package org.jazzteam.repository;

import org.jazzteam.model.PerformerEntity;
import org.jazzteam.model.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;

@Repository
public interface PerformerRepository extends JpaRepository<PerformerEntity, Integer > {
    default PerformerEntity findByIdOrThrow(Integer id) {
        return findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
