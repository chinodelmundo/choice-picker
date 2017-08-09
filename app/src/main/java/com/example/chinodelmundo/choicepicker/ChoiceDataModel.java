package com.example.chinodelmundo.choicepicker;

/**
 * Created by Chino.DelMundo on 8/3/2017.
 */

public class ChoiceDataModel {
    long id;
    long questionId;
    String text;

    public ChoiceDataModel(long id, long questionId, String text) {
        this.id = id;
        this.questionId = questionId;
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public long getQuestionId() {
        return questionId;
    }

    public String getText() {
        return text;
    }

}