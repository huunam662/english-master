package com.example.englishmaster_be.Model;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Hidden
@Entity
@Table(name = "Part")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Part implements Serializable {

    @Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
    UUID partId;

    @Column(name = "part_name")
    String partName;

    @Column(name = "part_description")
    String partDescription;

    @Column(name = "part_type")
    String partType;

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

    @ManyToMany(mappedBy = "parts")
	List<Topic> topics;

    public Part(String partName, String partDescription, String partType) {
        this.partName = partName;
        this.partDescription = partDescription;
        this.partType = partType;
    }

}
