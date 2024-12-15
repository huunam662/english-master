package com.example.englishmaster_be.model.user_answer_matching;

import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(hidden = true)
@Entity
@Table(name = "user_answer_matching")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAnswerMatchingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "content_left")
    String contentLeft;

    @Column(name = "content_right")
    String contentRight;

    @ManyToOne
    @JoinColumn(name = "question_id")
    QuestionEntity question;

    @ManyToOne
    @JoinColumn(name = "user_id")
    UserEntity user;
}
