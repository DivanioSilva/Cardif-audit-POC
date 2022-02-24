package com.cardif.cardifsyncpoc.controller;

import com.cardif.cardifsyncpoc.dto.UserDto;
import com.cardif.cardifsyncpoc.service.ClientService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("client/")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto save(@RequestBody UserDto dto){
        return this.clientService.save(dto);
    }


    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto update(@RequestBody UserDto dto){
        return this.clientService.update(dto);
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void delete(@RequestBody UserDto dto){
        this.clientService.delete(dto);
    }
}
