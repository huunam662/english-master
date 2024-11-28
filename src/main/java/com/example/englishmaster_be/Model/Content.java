package com.example.englishmaster_be.Model;

import com.example.englishmaster_be.DTO.Content.CreateContentDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "Content")
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Content implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID contentId;

    @Column(name = "topic_id")
    UUID topicId;

    @Column(name = "code")
    String code;

    @Column(name = "content_type")
    String contentType;

    @Column(name = "content_data")
    String contentData;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_at")
    LocalDateTime createAt = LocalDateTime.now();

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "update_at")
    LocalDateTime updateAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "create_by", referencedColumnName = "id")
    User userCreate;

    @ManyToOne
    @JoinColumn(name = "update_by", referencedColumnName = "id")
    User userUpdate;

    @ManyToOne
    @JoinTable(name = "question_content",
            joinColumns = @JoinColumn(name = "content_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    Question question;


    public Content(Question question, String contentType, String contentData) {
        this.question = question;
        this.contentType = contentType;
        this.contentData = contentData;
    }

    public Content(CreateContentDTO createContentDTO) {

        if(Objects.isNull(createContentDTO)) return;

        this.contentType = createContentDTO.getContentType();
        this.contentData = createContentDTO.getContentData();
        this.code = createContentDTO.getCode();
        this.topicId = createContentDTO.getTopicId();
    }


}
