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
@Table(name = "part")
public class Part implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID partId;

    @Column(name = "part_name")
    private String partName;

    @Column(name = "part_description")
    private String partDescription;

    @Column(name = "part_type")
    private String partType;

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

    @ManyToMany(mappedBy = "parts")
    private Collection<Topic> topics;

    public Part() {
        createAt = LocalDateTime.now();
        updateAt= LocalDateTime.now();
    }

    public Part(String partName, String partDescription, String partType) {
        this.partName = partName;
        this.partDescription = partDescription;
        this.partType = partType;

        createAt = LocalDateTime.now();
        updateAt= LocalDateTime.now();
    }
}
