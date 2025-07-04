package com.example.englishmaster_be.domain.speaking_submission.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.common.constant.speaking_test.SpeakingSendContent;
import com.example.englishmaster_be.common.constant.speaking_test.StatusSpeakingSubmission;
import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestKeyResponse;
import com.example.englishmaster_be.domain.mock_test.repository.jpa.MockTestRepository;
import com.example.englishmaster_be.domain.question.repository.jpa.QuestionRepository;
import com.example.englishmaster_be.domain.speaking_submission.dto.request.SpeakingQuestionAudioRequest;
import com.example.englishmaster_be.domain.speaking_submission.dto.request.SpeakingSubmitRequest;
import com.example.englishmaster_be.domain.speaking_submission.dto.response.speaking_error.BotFeedbackErrorResponse;
import com.example.englishmaster_be.domain.speaking_submission.dto.response.speaking_submission.BotFeedbackResponse;
import com.example.englishmaster_be.domain.speaking_submission.model.SpeakingErrorEntity;
import com.example.englishmaster_be.domain.speaking_submission.model.SpeakingSubmissionEntity;
import com.example.englishmaster_be.domain.speaking_submission.repository.jdbc.SpeakingErrorJdbcRepository;
import com.example.englishmaster_be.domain.speaking_submission.repository.jdbc.SpeakingSubmissionJdbcRepository;
import com.example.englishmaster_be.domain.speaking_submission.util.bot.BotUtil;
import com.example.englishmaster_be.domain.speaking_submission.util.speaking.SpeakingUtil;
import com.example.englishmaster_be.common.service.mailer.MailerService;
import com.example.englishmaster_be.domain.evaluator_writing.util.SpringApplicationContext;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j(topic = "SPEAKING-SUBMISSION-SERVICE")
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class SpeakingSubmissionService implements ISpeakingSubmissionService{

    SpeakingSubmissionJdbcRepository speakingSubmissionJdbcRepository;

    SpeakingErrorJdbcRepository speakingErrorJdbcRepository;

    MockTestRepository mockTestRepository;

    QuestionRepository questionRepository;

    MailerService mailerService;

    @Transactional
    @Override
    public MockTestKeyResponse speakingSubmitTest(SpeakingSubmitRequest speakingSubmitRequest) {
        // Ràng buộc dữ liệu đầu vào phải có giá trị
        Assert.notNull(speakingSubmitRequest, "Speaking contents to submit test is required.");
        Assert.notNull(speakingSubmitRequest.getMockTestId(), "Mock test id required.");
        if(speakingSubmitRequest.getContents() == null || speakingSubmitRequest.getContents().isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "Speaking contents to submit test is empty.");
        // Lấy instance mock test theo id từ database
        UUID mockTestId = speakingSubmitRequest.getMockTestId();
        if(!mockTestRepository.isExistedMockTest(mockTestId))
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Mock test not found.");
        // Khởi tạo danh sách lưu trữ record Speaking Submission để chuẩn bị lưu xuống database về sau
        List<SpeakingSubmissionEntity> speakingSubmissions = new ArrayList<>();
        List<SpeakingQuestionAudioRequest> questionAudios = speakingSubmitRequest.getContents();
        // Lấy ra danh sách question id ở dưới database ứng với question id được request từ client.
        List<UUID> questionIds = questionRepository.findQuestionIdsIn(questionAudios.stream().map(SpeakingQuestionAudioRequest::getQuestionId).toList());
        // Duyệt danh sách contents (question_id, audio_url) được gửi từ client.
        for(SpeakingQuestionAudioRequest questionAudioSubmit : speakingSubmitRequest.getContents()) {
            // Ràng buộc dữ liệu quan trọng được gói bên trong instance SpeakingSubmitRequest phải có dữ liệu
            Assert.notNull(questionAudioSubmit, "Speaking content to submit test is required.");
            Assert.notNull(questionAudioSubmit.getQuestionId(), "Question id to submit test is required.");
            // Nếu audio_url không hợp lệ thì duyệt tiếp
            if(questionAudioSubmit.getAudioUrl() == null || questionAudioSubmit.getAudioUrl().isEmpty())
                continue;
            // Nếu question_id không tồn tại dưới database thì duyệt tiếp
            if(!questionIds.contains(questionAudioSubmit.getQuestionId()))
                continue;
            // Khởi tạo các instance
            speakingSubmissions.add(
                    SpeakingSubmissionEntity.builder()
                            .id(UUID.randomUUID())
                            .audioUrl(questionAudioSubmit.getAudioUrl())
                            .mockTestId(mockTestId)
                            .questionId(questionAudioSubmit.getQuestionId())
                            .status(StatusSpeakingSubmission.Processing)
                            .build()
            );
        }
        // Lưu danh sách Speaking submissions xuống database (batch insert)
        speakingSubmissionJdbcRepository.batchInsertSpeakingSubmission(speakingSubmissions);
        // Tiến hành chia luồng mới để chấm điểm và đánh giá và spring sẽ không chờ vì đã tích hợp cơ chế bất đồng bộ (Async)
        CompletableFuture.runAsync(
                // Tiến hành goi hàm chấm điểm và đánh giá
                () -> evaluateSpeakingTest(speakingSubmissions)
        ).exceptionally((ex) -> {
            // Nếu có ngoại lệ xảy ra trong quá trình đánh giá thì log lại thông báo để dễ debug sau này
            log.error(ex.getMessage());
            return null;
        }).thenRun(() -> {
            // Sau khi chấm điểm và đánh giá các phần thi của thí sinh thành công thì cập nhật lại thông tin cho mock test
            int countOfQuestionTopicMockTest = questionRepository.countOfQuestionSpeakingTopicByMockTestId(mockTestId);
            int totalQuestionFinish = speakingSubmissions.size();
            int totalQuestionSkip = countOfQuestionTopicMockTest - speakingSubmissions.size();
            // Tính phần trăm trung bình theo từng phần thi (parts)
            float speakingReachedPercent = BigDecimal.valueOf(
                        speakingSubmissions.stream().map(SpeakingSubmissionEntity::getReachedPercent)
                        .reduce(0f, Float::sum) / totalQuestionFinish
                    ).setScale(2, RoundingMode.HALF_UP).floatValue();
            // Cập nhật mới cho mock test
            mockTestRepository.updateMockTest(
                    mockTestId, speakingReachedPercent, totalQuestionFinish, 0,
                    totalQuestionFinish, totalQuestionSkip, totalQuestionFinish
            );
        }).thenRun(() -> {
            // Gửi mail cho thí sinh về kết quả bài thi
            log.info("Send Email to User.");
            try {
                mailerService.sendResultMockTestEmail(mockTestId);
            } catch (MessagingException | IOException e) {
                log.error(e.getMessage());
            }
        });
        // Trả về mock test id (không chờ hàm async được khai báo ở trên)
        return MockTestKeyResponse.builder()
                .mockTestId(mockTestId)
                .build();
    }

    @Transactional
    @Override
    public void evaluateSpeakingTest(List<SpeakingSubmissionEntity> speakingSubmissions) {
        if(speakingSubmissions == null || speakingSubmissions.isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "Speaking submissions are required.");
        // Khởi tạo danh sách lưu trữ các task tương ứng với mỗi luồng được chia
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        // Duyệt danh sách các instance Speaking submission
        for(SpeakingSubmissionEntity speakingSubmission : speakingSubmissions){
            // Chia luồng cho mỗi instance được duyệt
            CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        // Lấy ra kết quả speech to text từ hàm giao tiếp với API bên thứ 3 (AssemblyAI open API)
                        String speakingText = BotUtil.speechToText(speakingSubmission.getAudioUrl());
                        // Lấy ra question content (Đề bài) từ question id được request từ client
                        String questionContent = questionRepository.findQuestionSpeakingContent(speakingSubmission.getQuestionId());
                        // Tiến hành xóa các thẻ (tag) tồn tại ở trong question content nếu có
                        String questionContentClearHtmlTag = Jsoup.clean(questionContent, Safelist.none());
                        // Gán để bài và đoạn speech to text vào trong prompt content, sẽ gửi cho API bên thứ 3 (coHere chatbot open API)
                        String sendContent = SpeakingSendContent.Content.getContent().replace(":question", questionContentClearHtmlTag)
                                .replace(":speakingText", speakingText);
                        // Gửi prompt cho chat bot
                        BotFeedbackResponse feedbackTestResult = BotUtil.feedback4Speaking(sendContent);
                        if(feedbackTestResult != null){
                            // Cập nhật nội dung chấm điểm và đánh giá từ AI cho phần thi của thí sinh
                            speakingSubmission.setFeedback(feedbackTestResult.getFeedback());
                            float reachedPercent = BigDecimal.valueOf(feedbackTestResult.getReachedPercent() / 100)
                                            .setScale(2, RoundingMode.HALF_UP).floatValue();
                            speakingSubmission.setReachedPercent(reachedPercent);
                            speakingSubmission.setLevelSpeaker(SpeakingUtil.toLevelSpeaker(feedbackTestResult.getReachedPercent()));
                            speakingSubmission.setSpeakingText(speakingText);
                            BotFeedbackErrorResponse feedbackError = feedbackTestResult.getErrors();
                            // Lấy ra các lỗi speaking của thí sinh tương ứng với 4 tiêu chí (Fluency, Vocabulary, Grammar, Pronunciation)
                            List<SpeakingErrorEntity> speakingErrors = SpeakingUtil.toSpeakingErrors(speakingSubmission.getId(), feedbackError);
                            // Lưu các lỗi speaking của thí sinh xuống database
                            speakingErrorJdbcRepository.batchInsertSpeakingError(speakingErrors);
                        }
                        // Trả về instance phần thi của thí sinh
                        return speakingSubmission;
                    } catch (InterruptedException e) {
                        log.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }, SpringApplicationContext.getBean("configTaskExecutor", ThreadPoolTaskExecutor.class)
            ).exceptionally((e) -> {
                // Nếu có lỗi xảy ra trong quá trình chấm điểm và đánh giá thì log lại thông báo và trả về null
                log.error(e.getMessage());
                return null;
            }).thenAccept((speakingSubmissionApply) -> {
                // Nếu instance phần thi của thí sinh nhận được là null (trả ề từ exceptionally) thí return
                if(speakingSubmissionApply == null) return;
                // Nếu thành công thì tiến hành cập nhật status
                speakingSubmissionApply.setStatus(StatusSpeakingSubmission.Completed);
                // Cập nhật record phần thi của thí sinh
                speakingSubmissionJdbcRepository.updateSpeakingSubmission(speakingSubmissionApply);
            });
            // Thêm hàm task async vào danh sách lưu trữ các task tương ứng với mỗi luồng được chia
            futures.add(future);
        }
        // Đăng ký sự kiện chờ tất cả các task đều dược hoàn thành (đồng nhất)
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }

}
