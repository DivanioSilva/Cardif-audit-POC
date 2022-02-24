package com.cardif.cardifsyncpoc.service;

import com.cardif.cardifsyncpoc.dto.UserDto;

public interface ClientService {

    UserDto save(UserDto dto);

    UserDto update(UserDto dto);

    void delete(UserDto dto);
}
