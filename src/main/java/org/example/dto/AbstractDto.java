package org.example.dto;

import lombok.Data;

@Data
public abstract class AbstractDto<T> {
    private T id;
}
