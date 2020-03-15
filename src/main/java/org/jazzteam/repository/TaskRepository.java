package org.jazzteam.repository;

import org.jazzteam.model.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {
    default TaskEntity findByIdOrThrow(Integer id) {
        return findById(id).orElseThrow(EntityNotFoundException::new);
    }
    List<TaskEntity> findAllByOrderByOrderId();
}
