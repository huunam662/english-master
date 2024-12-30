package com.example.englishmaster_be.model.question;

import com.example.englishmaster_be.common.constant.QuestionTypeEnum;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.content.ContentEntity;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.question_label.QuestionLabelEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.List;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID questionId;

    @Column(name = "question_content")
    String questionContent;

    @Column(name = "question_score")
    Integer questionScore;

    @Column(name = "question_explain_en")
    String questionExplainEn;

    @Column(name = "question_explain_vn")
    String questionExplainVn;

    @Column(name = "question_numberical")
    Integer questionNumberical;

    @Column(name="question_type")
    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::questions_type_enum")
    QuestionTypeEnum questionType;

    @Column(name = "number_choice",columnDefinition = "int default 1")
    Integer numberChoice;

    String title;

    @Column(name = "count_blank")
    Integer countBlank;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt;

    @ManyToOne
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    UserEntity userCreate;

    @ManyToOne
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    UserEntity userUpdate;

    @ManyToOne
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    PartEntity part;

    @ManyToOne
    @JoinColumn(name = "question_group", referencedColumnName = "id")
    QuestionEntity questionGroupParent;

    @ManyToMany(mappedBy = "questions")
    List<TopicEntity> topics;

    @OneToMany(mappedBy = "questionGroupParent")
    List<QuestionEntity> questionGroupChildren;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    List<AnswerEntity> answers;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    List<ContentEntity> contentCollection;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    List<QuestionLabelEntity> labels;

    @Column(name = "has_hints")
    Boolean hasHints;

    @PreRemove
    void preRemove(){
        topics.forEach(topic -> topic.getQuestions().remove(this));
        questionGroupChildren.forEach(question -> question.setQuestionGroupParent(null));
        answers.clear();
        contentCollection.clear();
    }


    @PrePersist
    void onCreate() {
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
