package com.example.englishmaster_be.Model;

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
@Table(name = "question_group")
public class QuestionGroup implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID questionGroupId;

    @Column(name = "question_content")
    private String questionContent;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "content_data")
    private String contentData;

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

    public QuestionGroup() {
        createAt = LocalDateTime.now();
        updateAt= LocalDateTime.now();
    }
}
