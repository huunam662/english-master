package com.example.englishmaster_be.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "detail_mocktest")
public class DetailMockTest implements Serializable {
    @Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
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
    }

	public UUID getDetailMockTestId() {
		return detailMockTestId;
	}

	public void setDetailMockTestId(UUID detailMockTestId) {
		this.detailMockTestId = detailMockTestId;
	}

	public MockTest getMockTest() {
		return mockTest;
	}

	public void setMockTest(MockTest mockTest) {
		this.mockTest = mockTest;
	}

	public Answer getAnswer() {
		return answer;
	}

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

	public LocalDateTime getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}

	public User getUserCreate() {
		return userCreate;
	}

	public void setUserCreate(User userCreate) {
		this.userCreate = userCreate;
	}

	public LocalDateTime getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(LocalDateTime updateAt) {
		this.updateAt = updateAt;
	}

	public User getUserUpdate() {
		return userUpdate;
	}

	public void setUserUpdate(User userUpdate) {
		this.userUpdate = userUpdate;
	};

	@Override
	public String toString() {
		return "DetailMockTest{" +
				"detailMockTestId=" + detailMockTestId +
				'}';
	}
}
