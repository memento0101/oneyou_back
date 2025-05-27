//package com.example.toygry.one_you.users.service;
//
//import com.example.toygry.one_you.common.exception.BaseException;
//import com.example.toygry.one_you.common.exception.OneYouStatusCode;
//import com.example.toygry.one_you.jooq.generated.tables.pojos.Users;
//import com.example.toygry.one_you.users.dto.UserInsertRequest;
//import com.example.toygry.one_you.users.repository.UsersRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//@AllArgsConstructor
//public class UsersService {
//
//    private final UsersRepository usersRepository;
//
//    public List<Users> getAllUsers() {
//        return usersRepository.findAllUsers();
//    }
//
//    public void insertUser(UserInsertRequest request) {
//        usersRepository.insertUser(request);
//    }
//
//    public Users getUserById(UUID id) {
//
//        return Optional.ofNullable(usersRepository.findById(id)).orElseThrow(() -> new BaseException(OneYouStatusCode.UserNotFound));
//    }
//}
