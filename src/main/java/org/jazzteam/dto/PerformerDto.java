package org.jazzteam.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class PerformerDto extends AbstractDto<Integer> implements Serializable {
    private static final long serialVersionUID = 7959675233455749868L;

    private Integer id;
    private String firstName;
    private String lastName;

    @Override
    public String toString() {
        return String.format("%s %s", getFirstName(), getLastName());
    }
}
