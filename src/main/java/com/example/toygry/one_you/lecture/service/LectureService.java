package com.example.toygry.one_you.lecture.service;

import com.example.toygry.one_you.common.exception.BaseException;
import com.example.toygry.one_you.common.exception.OneYouStatusCode;
import com.example.toygry.one_you.jooq.generated.tables.records.*;
import com.example.toygry.one_you.lecture.dto.*;
import com.example.toygry.one_you.lecture.repository.LectureAttachmentRepository;
import com.example.toygry.one_you.lecture.repository.LectureImageRepository;
import com.example.toygry.one_you.lecture.repository.LectureRepository;
import com.example.toygry.one_you.lecture.repository.StudentLectureRepository;
import com.example.toygry.one_you.video.dto.VideoUploadResponse;
import com.example.toygry.one_you.video.service.VimeoUploadService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@AllArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;
    private final StudentLectureRepository studentLectureRepository;
    private final LectureAttachmentRepository lectureAttachmentRepository;
    private final LectureImageRepository lectureImageRepository;
    private final VimeoUploadService vimeoUploadService;

    public LectureDetailResponse getLectureDetail(UUID userId, UUID lectureId) {
        LocalDateTime expireDate = studentLectureRepository.findLectureExpireDate(userId, lectureId);
        if (expireDate == null || expireDate.isBefore(LocalDateTime.now())) {
            throw new BaseException(OneYouStatusCode.LECTURE_FORBIDDEN);
        }

        int total = lectureRepository.countTotalLectureDetails(lectureId);
        int completed = lectureRepository.countCompletedLectureDetails(userId, lectureId);
        double progress = total > 0 ? (completed * 100.0) / total : 0.0;

        // 강의 제목 조회
        String lectureTitle = lectureRepository.findLectureTitle(lectureId);
        if (lectureTitle == null) {
            throw new BaseException(OneYouStatusCode.LECTURE_NOT_FOUND, "강의 정보를 찾을 수 없습니다.");
        }

        List<LectureDetailWithProgressResponse> rawList = lectureRepository.findLectureChaptersWithProgress(userId, lectureId);

        // 그룹핑
        Map<UUID, LectureDetailResponse.Chapter> chapterMap = new LinkedHashMap<>();
        for (LectureDetailWithProgressResponse row : rawList) {
            chapterMap
                    .computeIfAbsent(row.chapterId(),
                            id -> new LectureDetailResponse.Chapter(id, row.chapterTitle(), new ArrayList<>()))
                    .details().add(new LectureDetailResponse.Detail(row.detailId(), row.detailTitle(), row.type(),
                            Boolean.TRUE.equals(row.isCompleted())));
        }

        return new LectureDetailResponse(
                lectureId,
                lectureTitle,
                (int) ChronoUnit.DAYS.between(LocalDateTime.now(), expireDate),
                Math.round(progress * 10.0) / 10.0,
                new ArrayList<>(chapterMap.values()));
    }

    public LectureContentsResponse getLectureContentsDetail(UUID userId, LectureDetailRequest request) {

        LectureDetailRecord detail = lectureRepository.findLectureDetail(request.lectureDetailId());
        if (detail == null) {
            throw new BaseException(OneYouStatusCode.LECTURE_NOT_FOUND);
        }

        // 첨부파일 조회 (모든 타입 공통)
        List<LectureAttachmentResponse> attachments = lectureAttachmentRepository
                .findAttachmentsByLectureDetailId(request.lectureDetailId());

        if ("QUIZ".equalsIgnoreCase(request.type())) {
            List<LectureQuizRecord> quizList = lectureRepository.fetchLectureQuizzes(request.lectureDetailId());
            if (quizList.isEmpty()) {
                throw new BaseException(OneYouStatusCode.LECTURE_NOT_FOUND, "등록된 퀴즈가 없습니다.");
            }

            // 각 퀴즈의 옵션도 함께 조회
            List<LectureContentsResponse.QuizWithOptions> quizResponses = quizList.stream().map(quiz -> {
                List<LectureQuizOptionRecord> options = lectureRepository.fetchQuizOptions(quiz.getId());
                return LectureContentsResponse.QuizWithOptions.from(quiz, options);
            }).toList();

            return LectureContentsResponse.ofQuiz(detail, quizResponses, attachments);
        }
        LectureContentRecord content = lectureRepository.fetchLectureContent(request.lectureDetailId());
        if (content == null) {
            throw new BaseException(OneYouStatusCode.LECTURE_NOT_FOUND, "강의 내용이 없습니다.");
        }

        if ("VIDEO".equalsIgnoreCase(request.type())) {
            // 비디오 강의면 학생 과제 조회
            StudentReviewSubmissionRecord submission = lectureRepository
                    .fetchStudentReviewSubmission(request.lectureDetailId(), userId);
            // 비디오 정보 조회
            VideoRecord video = lectureRepository.fetchVideoByContentId(request.lectureDetailId());
            return LectureContentsResponse.ofVideo(detail, content, video, submission, attachments);
        }

        if ("LIVE".equalsIgnoreCase(request.type())) {
            // 라이브 비디오 정보 조회
            VideoRecord video = lectureRepository.fetchVideoByContentId(request.lectureDetailId());
            return LectureContentsResponse.ofLive(detail, content, video, attachments);
        }

        throw new BaseException(OneYouStatusCode.BAD_REQUEST, "알 수 없는 강의 유형입니다.");

    }

    public void submitStudentLink(UUID userId, StudentLinkSubmissionRequest request) {
        // 강의 상세 정보가 존재하는지 확인
        LectureDetailRecord lectureDetail = lectureRepository.findLectureDetail(request.lectureDetailId());
        if (lectureDetail == null) {
            throw new BaseException(OneYouStatusCode.LECTURE_NOT_FOUND, "해당 강의를 찾을 수 없습니다.");
        }

        // 학생 리뷰 제출 정보 저장 또는 업데이트
        lectureRepository.insertOrUpdateStudentReviewSubmission(userId, request.lectureDetailId(), request.reviewUrl());

    }

    public QuizGradingResponse gradeQuiz(UUID userId, QuizSubmissionRequest request) {
        // 강의 상세 정보가 존재하는지 확인
        LectureDetailRecord lectureDetail = lectureRepository.findLectureDetail(request.lectureDetailId());
        if (lectureDetail == null) {
            throw new BaseException(OneYouStatusCode.LECTURE_NOT_FOUND, "해당 강의를 찾을 수 없습니다.");
        }

        // 해당 강의의 모든 퀴즈 조회
        List<LectureQuizRecord> quizzes = lectureRepository.fetchLectureQuizzes(request.lectureDetailId());
        if (quizzes.isEmpty()) {
            throw new BaseException(OneYouStatusCode.LECTURE_NOT_FOUND, "등록된 퀴즈가 없습니다.");
        }

        List<QuizGradingResponse.QuizResult> results = new ArrayList<>();
        int correctAnswers = 0;

        // 각 답안을 채점
        for (QuizSubmissionRequest.QuizAnswer answer : request.answers()) {
            // 퀴즈 정보 조회
            LectureQuizRecord quiz = quizzes.stream()
                    .filter(q -> q.getId().equals(answer.quizId()))
                    .findFirst()
                    .orElseThrow(() -> new BaseException(OneYouStatusCode.BAD_REQUEST, "존재하지 않는 퀴즈입니다."));

            // 선택한 옵션 정보 조회
            LectureQuizOptionRecord selectedOption = lectureRepository.fetchQuizOption(answer.selectedOptionId());
            if (selectedOption == null) {
                throw new BaseException(OneYouStatusCode.BAD_REQUEST, "존재하지 않는 옵션입니다.");
            }

            // 정답 옵션 조회
            LectureQuizOptionRecord correctOption = lectureRepository.fetchCorrectOption(answer.quizId());
            if (correctOption == null) {
                throw new BaseException(OneYouStatusCode.BAD_REQUEST, "정답이 설정되지 않은 퀴즈입니다.");
            }

            // 정답 여부 확인
            boolean isCorrect = selectedOption.getIsCorrect();
            if (isCorrect) {
                correctAnswers++;
            }

            // 결과 추가 (hint 제거)
            results.add(new QuizGradingResponse.QuizResult(
                    quiz.getId(),
                    quiz.getQuestion(),
                    selectedOption.getId(),
                    selectedOption.getOptionText(),
                    correctOption.getId(),
                    correctOption.getOptionText(),
                    isCorrect
            ));
        }

        int totalQuestions = quizzes.size();
        int score = totalQuestions > 0 ? (correctAnswers * 100) / totalQuestions : 0;
        boolean isPerfectScore = correctAnswers == totalQuestions;

        return new QuizGradingResponse(
                request.lectureDetailId(),
                totalQuestions,
                correctAnswers,
                score,
                isPerfectScore,
                results
        );
    }

    public String updateLectureProgress(UUID userId, LectureProgressRequest request) {
        // 강의 상세 정보가 존재하는지 확인
        LectureDetailRecord lectureDetail = lectureRepository.findLectureDetail(request.lectureDetailId());
        if (lectureDetail == null) {
            throw new BaseException(OneYouStatusCode.LECTURE_NOT_FOUND, "해당 강의를 찾을 수 없습니다.");
        }

        // 학생 강의 진도 업데이트
        lectureRepository.insertOrUpdateLectureProgress(userId, request.lectureDetailId(), request.isCompleted());

        return request.isCompleted() ? "강의가 완료로 표시되었습니다." : "강의 완료가 취소되었습니다.";
    }

    // =========================== 강의 생성 관련 메서드 ===========================

    /**
     * 새로운 강의를 생성합니다.
     * @param request 강의 생성 요청 정보
     * @param images 강의 이미지 파일들
     * @param createdBy 강의 생성자 ID
     * @return 생성된 강의 정보
     */
    public LectureCreateResponse createLecture(LectureCreateRequest request, List<MultipartFile> images, UUID createdBy) {
        // 입력 유효성 검사
        validateLectureCreateRequest(request);

        // 강의 생성
        UUID lectureId = lectureRepository.insertLecture(
                request.title(),
                request.subtitle(),
                request.category(),
                request.teacherId(),
                request.description(),
                request.price(),
                request.period(),
                request.target(),
                request.url()
        );

        // 이미지 업로드 처리
        List<LectureCreateResponse.LectureImageInfo> imageInfos = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            imageInfos = uploadLectureImages(lectureId, images, createdBy);
        }

        // 생성된 강의 정보 조회
        LectureRepository.LectureWithTeacherInfo lectureInfo = lectureRepository.findLectureWithTeacher(lectureId);
        if (lectureInfo == null) {
            throw new BaseException(OneYouStatusCode.INTERNAL_SERVER_ERROR, "강의 생성 후 조회에 실패했습니다.");
        }

        return new LectureCreateResponse(
                lectureInfo.lectureId(),
                lectureInfo.title(),
                lectureInfo.subtitle(),
                lectureInfo.category(),
                lectureInfo.teacherName(),
                lectureInfo.description(),
                lectureInfo.price(),
                lectureInfo.period(),
                lectureInfo.target(),
                lectureInfo.url(),
                imageInfos,
                lectureInfo.createdAt()
        );
    }

    /**
     * 강의 이미지들을 업로드합니다.
     * @param lectureId 강의 ID
     * @param images 이미지 파일들
     * @param uploadedBy 업로드한 사용자 ID
     * @return 업로드된 이미지 정보 목록
     */
    private List<LectureCreateResponse.LectureImageInfo> uploadLectureImages(UUID lectureId, List<MultipartFile> images, UUID uploadedBy) {
        List<LectureCreateResponse.LectureImageInfo> imageInfos = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            MultipartFile image = images.get(i);

            // 이미지 파일 유효성 검사
            validateImageFile(image);

            try {
                // 파일 저장
                String storedFileName = generateStoredFileName(image.getOriginalFilename());
                String filePath = saveImageFile(image, storedFileName);

                // 첫 번째 이미지를 대표 이미지로 설정
                boolean isPrimary = (i == 0);
                if (isPrimary) {
                    // 기존 대표 이미지 해제
                    lectureImageRepository.clearPrimaryImage(lectureId);
                }

                // 데이터베이스에 저장
                UUID imageId = lectureImageRepository.insertLectureImage(
                        lectureId,
                        image.getOriginalFilename(),
                        storedFileName,
                        filePath,
                        image.getSize(),
                        image.getContentType(),
                        i + 1, // display_order
                        isPrimary,
                        null, // alt_text (향후 확장 가능)
                        null, // description (향후 확장 가능)
                        uploadedBy
                );

                imageInfos.add(new LectureCreateResponse.LectureImageInfo(
                        imageId,
                        image.getOriginalFilename(),
                        filePath,
                        isPrimary,
                        i + 1,
                        null,
                        null
                ));

            } catch (IOException e) {
                throw new BaseException(OneYouStatusCode.INTERNAL_SERVER_ERROR,
                        "이미지 파일 저장에 실패했습니다: " + e.getMessage());
            }
        }

        return imageInfos;
    }

    /**
     * 강의 생성 요청의 유효성을 검사합니다.
     * @param request 강의 생성 요청
     */
    private void validateLectureCreateRequest(LectureCreateRequest request) {
        if (request.title() == null || request.title().trim().isEmpty()) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "강의 제목은 필수입니다.");
        }

        if (request.category() == null || request.category().trim().isEmpty()) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "강의 카테고리는 필수입니다.");
        }

        if (request.teacherId() == null) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "강사 ID는 필수입니다.");
        }

        if (request.description() == null || request.description().trim().isEmpty()) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "강의 설명은 필수입니다.");
        }

        if (request.price() == null || request.price() < 0) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "강의 가격은 0 이상이어야 합니다.");
        }
    }

    /**
     * 이미지 파일의 유효성을 검사합니다.
     * @param imageFile 검사할 이미지 파일
     */
    private void validateImageFile(MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "이미지 파일이 선택되지 않았습니다.");
        }

        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "이미지 파일만 업로드 가능합니다.");
        }

        // 지원하는 이미지 타입 확인
        List<String> allowedTypes = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp");
        if (!allowedTypes.contains(contentType.toLowerCase())) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST,
                    "지원하지 않는 이미지 형식입니다. (jpg, png, gif, bmp만 지원)");
        }

        long maxSize = 10 * 1024 * 1024L; // 10MB
        if (imageFile.getSize() > maxSize) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "이미지 파일 크기는 10MB를 초과할 수 없습니다.");
        }
    }

    /**
     * 저장할 파일명을 생성합니다.
     * @param originalFileName 원본 파일명
     * @return 생성된 저장용 파일명
     */
    private String generateStoredFileName(String originalFileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        return "lecture_image_" + timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
    }

    /**
     * 이미지 파일을 실제로 저장합니다.
     * @param imageFile 저장할 이미지 파일
     * @param storedFileName 저장할 파일명
     * @return 저장된 파일의 경로
     * @throws IOException 파일 저장 실패 시
     */
    private String saveImageFile(MultipartFile imageFile, String storedFileName) throws IOException {
        // 업로드 디렉토리 경로 (월별로 구분)
        String uploadDir = "uploads/lecture_images/" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));

        // 디렉토리 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 파일 저장
        Path filePath = uploadPath.resolve(storedFileName);
        Files.copy(imageFile.getInputStream(), filePath);

        return "/" + uploadDir + "/" + storedFileName;
    }

    // =========================== 비디오 관련 메서드 ===========================

    /**
     * 강의 콘텐츠에 비디오 ID를 저장합니다.
     * @param lectureContentId 강의 콘텐츠 ID
     * @param vimeoVideoId Vimeo 비디오 ID
     */
    public void updateVideoId(UUID lectureContentId, String vimeoVideoId) {
        // 먼저 video 테이블에 레코드 생성
        UUID videoId = lectureRepository.createVideoRecord(vimeoVideoId);

        // lecture_content 테이블에 video_id 업데이트
        lectureRepository.updateLectureContentVideoId(lectureContentId, videoId);
    }

    /**
     * 강의 콘텐츠의 비디오 ID를 조회합니다.
     * @param lectureContentId 강의 콘텐츠 ID
     * @return Vimeo 비디오 ID (없으면 null)
     */
    public String getVideoId(UUID lectureContentId) {
        return lectureRepository.getVimeoVideoId(lectureContentId);
    }

    /**
     * 강의 콘텐츠에서 비디오 연결을 제거합니다.
     * @param lectureContentId 강의 콘텐츠 ID
     */
    public void removeVideoId(UUID lectureContentId) {
        lectureRepository.removeLectureContentVideoId(lectureContentId);
    }

    /**
     * Vimeo에 비디오를 업로드하고 강의 콘텐츠와 연결합니다.
     * @param lectureContentId 강의 콘텐츠 ID
     * @param videoFile 업로드할 비디오 파일
     * @param uploaderId 업로드하는 사용자 ID
     * @return 업로드 결과 응답
     */
    public VideoUploadResponse uploadVideoToVimeo(UUID lectureContentId, MultipartFile videoFile, UUID uploaderId) {
        // 파일 유효성 검사
        validateVideoFile(videoFile);

        try {
            // Vimeo에 비디오 업로드
            String vimeoVideoId = vimeoUploadService.uploadVideo(videoFile);

            // 강의 콘텐츠에 비디오 ID 저장
            updateVideoId(lectureContentId, vimeoVideoId);

            return VideoUploadResponse.builder()
                    .lectureContentId(lectureContentId)
                    .vimeoVideoId(vimeoVideoId)
                    .fileName(videoFile.getOriginalFilename())
                    .fileSize(videoFile.getSize())
                    .message("비디오 업로드가 완료되었습니다.")
                    .build();

        } catch (Exception e) {
            throw new BaseException(OneYouStatusCode.INTERNAL_SERVER_ERROR,
                    "비디오 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 강의 콘텐츠의 비디오 정보를 조회합니다.
     * @param lectureContentId 강의 콘텐츠 ID
     * @return 비디오 정보 응답
     */
    public VideoUploadResponse getVideoInfo(UUID lectureContentId) {
        String vimeoVideoId = getVideoId(lectureContentId);

        if (vimeoVideoId == null || vimeoVideoId.isEmpty()) {
            return VideoUploadResponse.builder()
                    .lectureContentId(lectureContentId)
                    .message("업로드된 비디오가 없습니다.")
                    .build();
        }

        return VideoUploadResponse.builder()
                .lectureContentId(lectureContentId)
                .vimeoVideoId(vimeoVideoId)
                .message("비디오 정보 조회 완료")
                .build();
    }

    /**
     * 비디오 파일 유효성을 검사합니다.
     * @param videoFile 검사할 비디오 파일
     */
    private void validateVideoFile(MultipartFile videoFile) {
        if (videoFile.isEmpty()) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "비디오 파일이 선택되지 않았습니다.");
        }

        String contentType = videoFile.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "비디오 파일만 업로드 가능합니다.");
        }

        long maxSize = 1024 * 1024 * 1024L; // 1GB
        if (videoFile.getSize() > maxSize) {
            throw new BaseException(OneYouStatusCode.BAD_REQUEST, "파일 크기는 1GB를 초과할 수 없습니다.");
        }
    }
}
