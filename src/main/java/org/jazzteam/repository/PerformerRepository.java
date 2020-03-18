package org.jazzteam.repository;

import org.jazzteam.model.PerformerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformerRepository extends CrudRepository<PerformerEntity, Integer> {
}
