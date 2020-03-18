package org.jazzteam.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class TaskDto extends AbstractDto<Integer> implements Serializable {
    private static final long serialVersionUID = -3767104767862361038L;

    private Integer id;
    private String name;
    private String description;
    private PerformerDto performer;
    private Integer orderId;
    private LocalDate executedAt;
}
