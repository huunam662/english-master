package com.example.englishmaster_be.domain.exam.question.model;

import com.example.englishmaster_be.common.constant.QuestionType;
import com.example.englishmaster_be.domain.exam.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.exam.part.model.PartEntity;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.model.SpeakingSubmissionEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "Question")
@Getter
@Setter
@NoArgsConstructor
@Schema(hidden = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionEntity {

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private UUID questionId;

    @Column(name = "question_title")
    private String questionTitle;

    @Column(name = "question_content")
    private String questionContent;

    @Column(name = "question_score")
    private Integer questionScore;

    @Column(name = "question_numberical")
    private Integer questionNumber;

    @Column(name = "content_audio")
    private String contentAudio;

    @Column(name = "content_image")
    private String contentImage;

    @Column(name = "question_result")
    private String questionResult;

    @Column(name="question_type")
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @Column(name = "number_choice", columnDefinition = "int default 1")
    private Integer numberChoice;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "is_question_parent")
    private Boolean isQuestionParent;

    @Column(name = "part_id", insertable = false, updatable = false)
    private UUID partId;

    @Column(name = "question_group", insertable = false, updatable = false)
    private UUID questionGroupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    private UserEntity userCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    private UserEntity userUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    private PartEntity part;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_group", referencedColumnName = "id")
    private QuestionEntity questionGroupParent;

    @OneToMany(mappedBy = "questionGroupParent", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<QuestionEntity> questionGroupChildren;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<AnswerEntity> answers;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<SpeakingSubmissionEntity> speakingSubmissions;

    @PrePersist
    @PreUpdate
    public void onPrePersist() {
        if(questionScore == null) questionScore = 0;
    }
}
