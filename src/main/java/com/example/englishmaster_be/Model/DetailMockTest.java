package com.example.englishmaster_be.Model;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Hidden
@Entity
@Table(name = "detail_mocktest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DetailMockTest implements Serializable {

    @Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
    UUID detailMockTestId;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt = LocalDateTime.now();

	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	@Column(name = "update_at")
	LocalDateTime updateAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "mock_test_id", referencedColumnName = "id")
    MockTest mockTest;

    @ManyToOne
    @JoinColumn(name = "answer_choose", referencedColumnName = "id")
    Answer answer;

    @ManyToOne
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    User userCreate;

    @ManyToOne
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    User userUpdate;


    public DetailMockTest(MockTest mockTest, Answer answer){
        this.mockTest = mockTest;
        this.answer = answer;
    }

	@Override
	public String toString() {
		return "DetailMockTest{" +
				"detailMockTestId=" + detailMockTestId +
				'}';
	}
}
