package org.jazzteam.repository;

import org.jazzteam.model.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {
    default TaskEntity findByIdOrThrow(Integer id) {
        return findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    default void updateOrders(TaskEntity swapTaskEntity, TaskEntity selectedTaskEntity) {
        save(swapTaskEntity);
        save(selectedTaskEntity);
    }

    List<TaskEntity> findAllByOrderByOrderId();

    @Modifying
    @Query("update TaskEntity t set t.orderId = t.orderId + 1 where t.orderId >= :nextOrder")
    void updateOrders(@Param("nextOrder") int nextOrder);

    @Query("select case when count(t) > 0 then true else false end from TaskEntity t where t.performer.id = :performerId")
    boolean existsPerformer(@Param("performerId") int performerId);
}
