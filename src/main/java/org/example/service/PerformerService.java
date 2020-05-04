package org.example.service;

import org.example.dto.PerformerDto;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface PerformerService {
    List<PerformerDto> getAll();

    List<PerformerDto> getAllOrdered(Sort sort);

    void deleteSelected(PerformerDto performerDto);

    void deleteById(Integer id);

    boolean isDeletable(int id);

    void create(PerformerDto performerDto);

    void update(PerformerDto performerDto);
}
