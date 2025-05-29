//package com.example.toygry.one_you.lecture.controller;
//
//import com.example.toygry.one_you.config.security.UserTokenPrincipal;
//import com.example.toygry.one_you.lecture.dto.LectureResponse;
//import com.example.toygry.one_you.lecture.dto.LectureUserResponse;
//import com.example.toygry.one_you.lecture.dto.UserLectureResponse;
//import com.example.toygry.one_you.lecture.service.LectureService;
//import lombok.AllArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/lectures")
//@RequiredArgsConstructor
//public class LectureController {
//
//    private final LectureService lectureService;
//
//    // 전체 강의 목록 출력
//    @GetMapping
//    public ResponseEntity<List<LectureResponse>> getAllLectures() {
//        List<LectureResponse> lectures = lectureService.getAllLecture();
//        return ResponseEntity.ok(lectures);
//    }
//
//    // TODO : Response 통일 가능 한지 확인하기
//    // 유저가 수강 하는 강의 list 출력
//    @GetMapping("/user")
//    public ResponseEntity<List<UserLectureResponse>> getUserLectures(@AuthenticationPrincipal UserTokenPrincipal userTokenPrincipal) {
//        return ResponseEntity.ok(lectureService.getUserLectures(userTokenPrincipal.getUuid()));
//    }
//
//    // 강의를 수강 하는 user list 출력
//    @GetMapping("/{lectureId}/user")
//    public ResponseEntity<LectureUserResponse> getLectureUsers(@PathVariable("lectureId") UUID lectureId) {
//        return ResponseEntity.ok(lectureService.getLectureWithUsers(lectureId));
//    }
//
//}
