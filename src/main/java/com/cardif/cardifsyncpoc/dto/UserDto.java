package com.cardif.cardifsyncpoc.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDto implements Serializable {
    private final Long id;
    private final String name;
    private final Integer age;
    private final String email;
    private final String actionOwner;
}
