package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.*;

public interface ICommentService {
    void save(Comment comment);

    boolean checkCommentParent(Comment comment);
}
