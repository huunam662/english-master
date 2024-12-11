package com.example.englishmaster_be.Model;

import com.example.englishmaster_be.DTO.MockTest.SaveMockTestDTO;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name = "mock_test")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID mockTestId;

    int score;

    @JoinColumn(name = "correct_answers")
    int correctAnswers;

    Time time;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt = LocalDateTime.now();

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

    @ManyToOne
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    Topic topic;

    @ManyToOne
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    User userCreate;

    @ManyToOne
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    User userUpdate;

    @OneToMany(mappedBy = "mockTest",cascade = CascadeType.ALL,orphanRemoval = true)
    List<DetailMockTest> detailMockTests;

    @OneToMany(mappedBy = "mockTest",cascade = CascadeType.ALL,orphanRemoval = true)
    List<ResultMockTest> resultMockTests;


    public MockTest(SaveMockTestDTO createMockTestDTO) {

        if(Objects.isNull(createMockTestDTO)) return;

        this.score = createMockTestDTO.getScore();
        this.time = createMockTestDTO.getTime();
    }

}
