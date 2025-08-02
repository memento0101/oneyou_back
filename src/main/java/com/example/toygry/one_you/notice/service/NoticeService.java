package com.example.toygry.one_you.notice.service;

import com.example.toygry.one_you.common.exception.BaseException;
import com.example.toygry.one_you.common.exception.OneYouStatusCode;
import com.example.toygry.one_you.jooq.generated.tables.records.NoticeRecord;
import com.example.toygry.one_you.notice.dto.NoticeRequest;
import com.example.toygry.one_you.notice.dto.NoticeResponse;
import com.example.toygry.one_you.notice.dto.NoticeUpdateRequest;
import com.example.toygry.one_you.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<NoticeResponse> getNoticeList(int page, int size) {
        int offset = page * size;
        return noticeRepository.findAllNotices(offset, size);
    }

    @Transactional
    public NoticeResponse getNotice(String noticeId) {
        UUID noticeUuid = UUID.fromString(noticeId);

        // 조회수 증가
        noticeRepository.incrementViewCount(noticeUuid);

        return noticeRepository.findNoticeById(noticeUuid)
                .orElseThrow(() -> new BaseException(OneYouStatusCode.NOTICE_NOT_FOUND));
    }

    public List<NoticeResponse> getTopNotices(int limit) {
        return noticeRepository.findTopNotices(limit);
    }

    @Transactional
    public String createNotice(UUID authorId, NoticeRequest request) {
        NoticeRecord record = noticeRepository.createNotice(
                authorId,
                request.title(),
                request.content(),
                request.isImportant()
        );

        return record.getId().toString();
    }

    @Transactional
    public String updateNotice(UUID authorId, String noticeId, NoticeUpdateRequest request) {
        UUID noticeUuid = UUID.fromString(noticeId);

        // 공지사항 존재 여부 및 작성자 확인
        NoticeRecord existingRecord = noticeRepository.findNoticeRecordById(noticeUuid)
                .orElseThrow(() -> new BaseException(OneYouStatusCode.NOTICE_NOT_FOUND));

        // 작성자가 본인인지 확인 (관리자 권한 체크는 추후 추가 가능)
        if (!existingRecord.getAuthorId().equals(authorId)) {
            throw new BaseException(OneYouStatusCode.UserForbidden);
        }

        NoticeRecord updatedRecord = noticeRepository.updateNotice(
                noticeUuid,
                request.title(),
                request.content(),
                request.isImportant()
        );

        return updatedRecord.getId().toString();
    }

    @Transactional
    public void deleteNotice(UUID authorId, String noticeId) {
        UUID noticeUuid = UUID.fromString(noticeId);

        // 공지사항 존재 여부 및 작성자 확인
        NoticeRecord existingRecord = noticeRepository.findNoticeRecordById(noticeUuid)
                .orElseThrow(() -> new BaseException(OneYouStatusCode.NOTICE_NOT_FOUND));

        // 작성자가 본인인지 확인 (관리자 권한 체크는 추후 추가 가능)
        if (!existingRecord.getAuthorId().equals(authorId)) {
            throw new BaseException(OneYouStatusCode.UserForbidden);
        }

        if (!noticeRepository.deleteNotice(noticeUuid)) {
            throw new BaseException(OneYouStatusCode.NOTICE_NOT_FOUND);
        }
    }
}