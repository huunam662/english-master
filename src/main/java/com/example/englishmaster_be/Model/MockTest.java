package com.example.englishmaster_be.Model;

import com.example.englishmaster_be.DTO.MockTest.CreateMockTestDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
@Data
@Entity
@Table(name = "mock_test")
public class MockTest implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID mockTestId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private int score;

    @JoinColumn(name = "correct_answers")
    private int correctAnswers;

    private Time time;

    @ManyToOne
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

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

    @OneToMany(mappedBy = "mockTest")
    private Collection<DetailMockTest> detailMockTests;

    @OneToMany(mappedBy = "mockTest")
    private Collection<ResultMockTest> resultMockTests;


    public MockTest() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    public MockTest(CreateMockTestDTO createMockTestDTO) {
        this.score = createMockTestDTO.getScore();
        this.time = createMockTestDTO.getTime();

        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

}
