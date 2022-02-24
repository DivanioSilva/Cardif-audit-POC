package com.cardif.cardifsyncpoc.service;

import com.cardif.cardifsyncpoc.domain.Audit;
import com.cardif.cardifsyncpoc.domain.User;
import com.cardif.cardifsyncpoc.dto.UserDto;
import com.cardif.cardifsyncpoc.mapper.ClientMapper;
import com.cardif.cardifsyncpoc.repository.AuditRepository;
import com.cardif.cardifsyncpoc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ClientServiceImpl implements ClientService{
    private final UserRepository userRepository;
    private final AuditRepository auditRepository;
    private final ClientMapper clientMapper;

    @Autowired
    public ClientServiceImpl(UserRepository userRepository,
                             AuditRepository auditRepository,
                             ClientMapper clientMapper) {
        this.userRepository = userRepository;
        this.auditRepository = auditRepository;
        this.clientMapper = clientMapper;
    }

    @Override
    public UserDto save(UserDto dto) {
        User user = clientMapper.userDtoToUser(dto);
        User toReturn = this.userRepository.save(user);
        this.saveAudit(toReturn, "save", dto.getActionOwner());
        UserDto userDto = this.clientMapper.userToUserDto(toReturn);
        return userDto;
    }

    @Override
    public UserDto update(UserDto dto) {
        User user = clientMapper.userDtoToUser(dto);
        User toReturn = this.userRepository.save(user);
        this.saveAudit(toReturn, "update", dto.getActionOwner());
        UserDto userDto = this.clientMapper.userToUserDto(toReturn);
        return userDto;
    }

    @Override
    public void delete(UserDto dto) {
        User user = clientMapper.userDtoToUser(dto);
        User toReturn = this.userRepository.save(user);
        this.saveAudit(toReturn, "delete", dto.getActionOwner());
    }

    private void saveAudit(User user, String action, String actionOwner){
        Audit audit = Audit.builder()
                .action(action)
                .date(new Date())
                .entityId(user.getId())
                .actionOwner(actionOwner)
                .build();

        this.auditRepository.save(audit);
    }
}
