package com.example.englishmaster_be.Model;

import com.example.englishmaster_be.Model.Response.AnswerResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "detail_mocktest")
public class DetailMockTest implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID detailMockTestId;

    @ManyToOne
    @JoinColumn(name = "mock_test_id", referencedColumnName = "id")
    private MockTest mockTest;

    @ManyToOne
    @JoinColumn(name = "answer_choose", referencedColumnName = "id")
    private Answer answer;

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

    public DetailMockTest() {
        createAt = LocalDateTime.now();
        updateAt= LocalDateTime.now();
    }

    public  DetailMockTest(MockTest mockTest, Answer answer){
        this.mockTest = mockTest;
        this.answer = answer;

        createAt = LocalDateTime.now();
        updateAt= LocalDateTime.now();
    };
}
