package com.example.englishmaster_be.dto.comment;

public class CreateCommentDTO {
    private String commentContent;

    public CreateCommentDTO() {
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
