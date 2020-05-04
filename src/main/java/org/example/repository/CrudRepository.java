package org.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityNotFoundException;

@NoRepositoryBean
public interface CrudRepository<E, ID> extends JpaRepository<E, ID> {
    default E findByIdOrThrow(ID id) {
        return findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
