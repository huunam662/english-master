package com.example.englishmaster_be.Model;

import com.example.englishmaster_be.DTO.Question.*;
import com.example.englishmaster_be.DTO.Type.QuestionType;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Hidden
@Entity
@Table(name = "Question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Question implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID questionId;

    @Column(name = "question_content")
    String questionContent;

    @Column(name = "question_score")
    int questionScore;

    @Column(name = "question_explain_en")
    String questionExplainEn;

    @Column(name = "question_explain_vn")
    String questionExplainVn;

    @Column(name = "question_numberical")
    int questionNumberical;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt = LocalDateTime.now();

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    User userCreate;

    @ManyToOne
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    User userUpdate;

    @ManyToOne
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    Part part;

    @ManyToOne
    @JoinColumn(name = "question_group", referencedColumnName = "id")
    Question questionGroup;

    @ManyToMany(mappedBy = "questions")
    List<Topic> topics;

    @OneToMany(mappedBy = "question")
    List<Answer> answers;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    List<Content> contentCollection;

    @Column(name="question_type")
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    public Question(SaveQuestionDTO createQuestionDTO) {

        if(Objects.isNull(createQuestionDTO)) return;

        this.questionContent = createQuestionDTO.getQuestionContent();
        this.questionScore = createQuestionDTO.getQuestionScore();
        this.questionExplainEn = createQuestionDTO.getQuestionExplainEn();
        this.questionExplainVn = createQuestionDTO.getQuestionExplainVn();
    }

    public Question(String questionContent, int questionScore) {
        this.questionContent = questionContent;
        this.questionScore = questionScore;
    }

}
