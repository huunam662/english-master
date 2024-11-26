package com.example.englishmaster_be.Model;

import com.example.englishmaster_be.DTO.MockTest.CreateResultMockTestDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "result_mocktest")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResultMockTest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID resultMockTestId;

    @ManyToOne
    @JoinColumn(name = "mock_test_id", referencedColumnName = "id")
    private MockTest mockTest;

    @ManyToOne
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    private Part part;

    @JoinColumn(name = "correct_answer")
    int correctAnswer;

    @JoinColumn(name = "score")
    int score;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @ManyToOne
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    private User userCreate;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @ManyToOne
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    private User userUpdate;

    public ResultMockTest(CreateResultMockTestDTO mockTestDTO) {
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();

        this.correctAnswer = mockTestDTO.getCorrectAnswer();
        this.score = mockTestDTO.getScore();
    }

    public ResultMockTest() {

    }
}
