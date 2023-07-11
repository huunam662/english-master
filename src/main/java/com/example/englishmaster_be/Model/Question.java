package com.example.englishmaster_be.Model;

import com.example.englishmaster_be.DTO.Question.*;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Entity
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

	public UUID getQuestionId() {
		return questionId;
	}

	public void setQuestionId(UUID questionId) {
		this.questionId = questionId;
	}

	public String getQuestionContent() {
		return questionContent;
	}

	public void setQuestionContent(String questionContent) {
		this.questionContent = questionContent;
	}

	public int getQuestionScore() {
		return questionScore;
	}

	public void setQuestionScore(int questionScore) {
		this.questionScore = questionScore;
	}

	public Question getQuestionGroup() {
		return questionGroup;
	}

	public void setQuestionGroup(Question questionGroup) {
		this.questionGroup = questionGroup;
	}

	public int getQuestionNumberical() {
		return questionNumberical;
	}

	public void setQuestionNumberical(int questionNumberical) {
		this.questionNumberical = questionNumberical;
	}

	public Part getPart() {
		return part;
	}

	public void setPart(Part part) {
		this.part = part;
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

	public Collection<Topic> getTopics() {
		return topics;
	}

	public void setTopics(Collection<Topic> topics) {
		this.topics = topics;
	}

	public Collection<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(Collection<Answer> answers) {
		this.answers = answers;
	}

	public Collection<Content> getContentCollection() {
		return contentCollection;
	}

	public void setContentCollection(Collection<Content> contentCollection) {
		this.contentCollection = contentCollection;
	}
    
}
