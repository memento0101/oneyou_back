package com.example.toygry.one_you.lecture.controller;

import com.example.toygry.one_you.lecture.dto.LectureResponse;
import com.example.toygry.one_you.lecture.service.LectureService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lectures")
@AllArgsConstructor
public class LectureController {

    private final LectureService lectureService;

    @GetMapping
    public ResponseEntity<List<LectureResponse>> getAllLectures() {
        List<LectureResponse> lectures = lectureService.getAllLecture();
        return ResponseEntity.ok(lectures);
    }
}
