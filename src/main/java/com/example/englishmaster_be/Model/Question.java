package com.example.englishmaster_be.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "question")
public class Question implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID questionId;

    @Column(name = "question_content")
    private String questionContent;

    @Column(name = "question_score")
    private String questionScore;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "content_data")
    private String contentData;

    @ManyToOne
    @JoinColumn(name = "question_group_id", referencedColumnName = "id")
    private QuestionGroup questionGroup;

    @ManyToOne
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    private Part part;


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

    @ManyToMany(mappedBy = "questions")
    private Collection<Topic> topics;

    @OneToMany(mappedBy = "question")
    private Collection<Answer> answers;

    public Question() {
        createAt = LocalDateTime.now();
        updateAt= LocalDateTime.now();
    }
}
