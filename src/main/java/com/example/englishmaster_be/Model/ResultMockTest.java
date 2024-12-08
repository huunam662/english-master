package com.example.englishmaster_be.Model;

import com.example.englishmaster_be.DTO.MockTest.SaveResultMockTestDTO;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name = "result_mocktest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResultMockTest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID resultMockTestId;

    @JoinColumn(name = "correct_answer")
    int correctAnswer;

    @JoinColumn(name = "score")
    int score;

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
    @JoinColumn(name = "mock_test_id", referencedColumnName = "id")
    MockTest mockTest;

    public ResultMockTest(SaveResultMockTestDTO mockTestDTO) {

        if(Objects.isNull(mockTestDTO)) return;

        this.correctAnswer = mockTestDTO.getCorrectAnswer();
        this.score = mockTestDTO.getScore();
    }

}
