package com.example.forum.controller.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class CommentForm {
    private int id;
    private String comment;
    private int reportId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
