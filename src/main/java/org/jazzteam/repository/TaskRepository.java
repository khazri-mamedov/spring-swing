package org.jazzteam.repository;

import org.jazzteam.model.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {
    default void findByIdOrThrow(Integer id) {
        findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
