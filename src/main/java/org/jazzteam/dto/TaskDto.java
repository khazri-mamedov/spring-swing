package org.jazzteam.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class TaskDto implements Serializable {
    private static final long serialVersionUID = -3767104767862361038L;

    private Integer id;
    private String name;
    private String description;
    private ExecutorDto executor;
    private LocalDate executedAt;
}
