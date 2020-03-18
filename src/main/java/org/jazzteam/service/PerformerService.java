package org.jazzteam.service;

import org.jazzteam.dto.PerformerDto;

import java.util.List;

public interface PerformerService {
    List<PerformerDto> getAllPerformers();

    void deleteSelectedPerformer(PerformerDto performerDto);

    void deleteById(int id);

    boolean isDeletable(int id);

    void createPerformer(PerformerDto performerDto);

    void updatePerformer(PerformerDto performerDto);
}
