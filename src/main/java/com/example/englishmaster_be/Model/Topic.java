package com.example.englishmaster_be.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "topics")
public class Topic implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID topicId;

    @Column(name = "topic_name")
    private String topicName;

    @Column(name = "topic_image")
    private String topicImage;

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


    public Topic() {
        createAt = LocalDateTime.now();
        updateAt= LocalDateTime.now();
    }

    public Topic(String topicName, String topicImage, String topicDescription, String topicType, String workTime, LocalDateTime startTime, LocalDateTime endTime, boolean enable) {
        this.topicName = topicName;
        this.topicImage = topicImage;
        this.topicDescription = topicDescription;
        this.topicType = topicType;
        this.workTime = workTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.enable = enable;

        createAt = LocalDateTime.now();
        updateAt= LocalDateTime.now();
    }
}
