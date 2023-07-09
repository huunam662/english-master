package com.example.englishmaster_be.Model;

import com.example.englishmaster_be.DTO.Question.*;
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
    private int questionScore;

    @ManyToOne
    @JoinColumn(name = "question_group", referencedColumnName = "id")
    private Question questionGroup;

    @Column(name = "question_numberical")
    private int questionNumberical;

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

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private Collection<Content> contentCollection;



    public Question() {
        createAt = LocalDateTime.now();
        updateAt= LocalDateTime.now();
    }

    public Question(CreateQuestionDTO createQuestionDTO){
        this.questionContent = createQuestionDTO.getQuestionContent();
        this.questionScore = createQuestionDTO.getQuestionScore();

        createAt = LocalDateTime.now();
        updateAt= LocalDateTime.now();
    }

    public Question(String questionContent, int questionScore){
        this.questionContent = questionContent;
        this.questionScore = questionScore;

        createAt = LocalDateTime.now();
        updateAt= LocalDateTime.now();
    }
}
