package com.example.englishmaster_be.Model;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(name = "flash_card")
public class FlashCard implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID flashCardId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "title")
    private String flashCardTitle;

    @Column(name="image")
    private String flashCardImage;

    @Column(name = "description")
    private String flashCardDescription;


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
    @OneToMany(mappedBy = "flashCard", cascade = CascadeType.ALL)
    private Collection<FlashCardWord> flashCardWords;

    public FlashCard() {
        createAt = LocalDateTime.now();
        updateAt= LocalDateTime.now();
    }

    public UUID getFlashCardId() {
        return flashCardId;
    }

    public void setFlashCardId(UUID flashCardId) {
        this.flashCardId = flashCardId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFlashCardTitle() {
        return flashCardTitle;
    }

    public void setFlashCardTitle(String flashCardTitle) {
        this.flashCardTitle = flashCardTitle;
    }

    public String getFlashCardDescription() {
        return flashCardDescription;
    }

    public void setFlashCardDescription(String flashCardDescription) {
        this.flashCardDescription = flashCardDescription;
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

    public Collection<FlashCardWord> getFlashCardWords() {
        return flashCardWords;
    }

    public void setFlashCardWords(Collection<FlashCardWord> flashCardWords) {
        this.flashCardWords = flashCardWords;
    }

    public String getFlashCardImage() {
        return flashCardImage;
    }

    public void setFlashCardImage(String flashCardImage) {
        this.flashCardImage = flashCardImage;
    }
}
