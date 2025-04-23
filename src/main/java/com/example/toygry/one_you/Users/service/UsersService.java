package com.example.toygry.one_you.Users.service;

import com.example.toygry.one_you.Users.dto.UserInsertRequest;
import com.example.toygry.one_you.Users.repository.UsersRepository;
import com.example.toygry.one_you.jooq.generated.tables.pojos.Users;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    public List<Users> getAllUsers() {
        return usersRepository.findAllUsers();
    }

    public void insertUser(UserInsertRequest request) {
        usersRepository.insertUser(request);
    }
}
