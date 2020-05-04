package org.example.repository;

import org.example.model.PerformerEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformerRepository extends CrudRepository<PerformerEntity, Integer> {
}
