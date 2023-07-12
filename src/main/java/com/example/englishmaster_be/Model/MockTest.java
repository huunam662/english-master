package com.example.englishmaster_be.Model;

import com.example.englishmaster_be.DTO.MockTest.CreateMockTestDTO;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

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


    public MockTest() {
        createAt = LocalDateTime.now();
        updateAt= LocalDateTime.now();
    }

    public  MockTest(CreateMockTestDTO createMockTestDTO){
        this.score = createMockTestDTO.getScore();
        this.time = createMockTestDTO.getTime();

        createAt = LocalDateTime.now();
        updateAt= LocalDateTime.now();
    }

	public UUID getMockTestId() {
		return mockTestId;
	}

	public void setMockTestId(UUID mockTestId) {
		this.mockTestId = mockTestId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
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
	}

	public Collection<DetailMockTest> getDetailMockTests() {
		return detailMockTests;
	}

	public void setDetailMockTests(Collection<DetailMockTest> detailMockTests) {
		this.detailMockTests = detailMockTests;
	}
    
    
}
