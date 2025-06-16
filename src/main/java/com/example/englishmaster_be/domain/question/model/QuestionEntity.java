package com.example.englishmaster_be.domain.question.model;

import com.example.englishmaster_be.common.constant.QuestionType;
import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.mock_test_result.model.MockTestDetailEntity;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.speaking_submission.model.SpeakingSubmissionEntity;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "Question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(hidden = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionEntity {

    @Id
    @Column(name = "id")
    UUID questionId;

    @Column(name = "question_title")
    String questionTitle;

    @Column(name = "question_content", columnDefinition = "text")
    String questionContent;

    @Column(name = "question_score")
    Integer questionScore;

    @Column(name = "content_audio")
    String contentAudio;

    @Column(name = "content_image")
    String contentImage;

    @Column(name = "question_result")
    String questionResult;

    @Column(name = "question_explain_en")
    String questionExplainEn;

    @Column(name = "question_explain_vn")
    String questionExplainVn;

    @Column(name="question_type")
    @Enumerated(EnumType.STRING)
    QuestionType questionType;

    @Column(name = "number_choice", columnDefinition = "int default 1")
    Integer numberChoice;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt;

    @Column(name = "has_hints")
    Boolean hasHints;

    @Column(name = "is_question_parent")
    Boolean isQuestionParent;

    @Column(name = "part_id", insertable = false, updatable = false)
    UUID partId;

    @Column(name = "question_group", insertable = false, updatable = false)
    UUID questionGroupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    UserEntity userCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    UserEntity userUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    PartEntity part;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_group", referencedColumnName = "id")
    QuestionEntity questionGroupParent;

    @OneToMany(mappedBy = "questionGroupParent", fetch = FetchType.LAZY)
    Set<QuestionEntity> questionGroupChildren;

    @OneToMany(mappedBy = "question", orphanRemoval = true, fetch = FetchType.LAZY)
    Set<AnswerEntity> answers;

    @OneToMany(mappedBy = "questionChild", fetch = FetchType.LAZY)
    Set<MockTestDetailEntity> detailMockTests;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    Set<SpeakingSubmissionEntity> speakingSubmissions;

    @PrePersist
    void onCreate() {

        if(questionId == null)
            questionId = UUID.randomUUID();

        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();

        if(questionScore == null) questionScore = 0;
    }

    @PreUpdate
    void onUpdate() {
        updateAt = LocalDateTime.now();

        if(questionScore == null) questionScore = 0;
    }

}
