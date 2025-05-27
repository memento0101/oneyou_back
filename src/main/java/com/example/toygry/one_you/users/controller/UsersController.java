//package com.example.toygry.one_you.users.controller;
//
//import com.example.toygry.one_you.config.security.UserTokenPrincipal;
//import com.example.toygry.one_you.jooq.generated.tables.pojos.Users;
//import com.example.toygry.one_you.users.dto.UserInsertRequest;
//import com.example.toygry.one_you.users.service.UsersService;
//import lombok.AllArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/users")
//@AllArgsConstructor
//public class UsersController {
//
//    private final UsersService usersService;
//
//    @GetMapping
//    public List<Users> getAllUsers() { // 이거 Pojo 인데 구분이 좀 어려워서 변경 필요
//        return usersService.getAllUsers();
//    }
//
//    @PostMapping
//    public ResponseEntity<Void> insertUser(@RequestBody UserInsertRequest request) {
//        usersService.insertUser(request);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
//
//    // 유저 정보 한개 가져오기
//    @GetMapping("/info")
//    public ResponseEntity<Users> getUserInfo(@AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal) {
//        return ResponseEntity.ok(usersService.getUserById(userTokenPrincipal.getUuid()));
//    }
//}
