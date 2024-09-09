package com.example.englishmaster_be.Model;

import com.example.englishmaster_be.DTO.Content.CreateContentDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "content")
public class Content implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID contentId;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "content_data")
    private String contentData;

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

    @ManyToOne
    @JoinTable(name = "question_content",
            joinColumns = @JoinColumn(name = "content_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    private Question question;

    @Column(name = "topic_id")
    private UUID topicId;

    @Column(name = "code")
    private String code;


    public Content(Question question, String contentType, String contentData) {
        this.question = question;
        this.contentType = contentType;
        this.contentData = contentData;

        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    public Content() {

    }

    public Content(CreateContentDTO createContentDTO) {
        this.contentType = createContentDTO.getContentType();
        this.contentData = createContentDTO.getContentData();
        this.code = createContentDTO.getCode();
        this.topicId = createContentDTO.getTopicId();

        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }


}
