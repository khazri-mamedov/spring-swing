package org.example.repository;

import org.example.model.TaskEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<TaskEntity, Integer> {
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
