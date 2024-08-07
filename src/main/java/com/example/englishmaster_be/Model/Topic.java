package com.example.englishmaster_be.Model;

import jakarta.persistence.*;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(name = "topics")
@ToString
public class Topic implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID topicId;

    @Column(name = "topic_name")
    private String topicName;

    @Column(name = "topic_image")
    private String topicImage;

    @Column(name = "number_question")
    private int numberQuestion;
    @Column(name = "topic_description")
    private String topicDescription;

    @Column(name = "topic_type")
    private String topicType;

    @Column(name = "work_time")
    private String workTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "topic")
    private Collection<Comment> comments;
    @ManyToMany
    @JoinTable(name = "topic_question",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    private Collection<Question> questions;

    @ManyToMany
    @JoinTable(name = "topic_part",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "part_id"))
    private Collection<Part> parts;

    private boolean enable;

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

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private Collection<MockTest> mockTests;

    @ManyToOne
    @JoinColumn(name = "pack_id", referencedColumnName = "id")
    private Pack pack;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private Status status;

    public Topic() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    public Topic(String topicName, String topicImage, String topicDescription, String topicType, String workTime, LocalDateTime startTime, LocalDateTime endTime) {
        this.topicName = topicName;
        this.topicImage = topicImage;
        this.topicDescription = topicDescription;
        this.topicType = topicType;
        this.workTime = workTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.enable = true;

        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    public UUID getTopicId() {
        return topicId;
    }

    public void setTopicId(UUID topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicImage() {
        return topicImage;
    }

    public void setTopicImage(String topicImage) {
        this.topicImage = topicImage;
    }

    public int getNumberQuestion() {
        return numberQuestion;
    }

    public void setNumberQuestion(int numberQuestion) {
        this.numberQuestion = numberQuestion;
    }

    public String getTopicDescription() {
        return topicDescription;
    }

    public void setTopicDescription(String topicDescription) {
        this.topicDescription = topicDescription;
    }

    public String getTopicType() {
        return topicType;
    }

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Collection<Comment> getComments() {
        return comments;
    }

    public void setComments(Collection<Comment> comments) {
        this.comments = comments;
    }

    public Collection<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Collection<Question> questions) {
        this.questions = questions;
    }

    public Collection<Part> getParts() {
        return parts;
    }

    public void setParts(Collection<Part> parts) {
        this.parts = parts;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
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

    public Collection<MockTest> getMockTests() {
        return mockTests;
    }

    public void setMockTests(Collection<MockTest> mockTests) {
        this.mockTests = mockTests;
    }

    public Pack getPack() {
        return pack;
    }

    public void setPack(Pack pack) {
        this.pack = pack;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
