package org.jazzteam.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PerformerDto implements Serializable {
    private static final long serialVersionUID = 7959675233455749868L;

    private Integer id;
    private String firstName;
    private String lastName;
}
