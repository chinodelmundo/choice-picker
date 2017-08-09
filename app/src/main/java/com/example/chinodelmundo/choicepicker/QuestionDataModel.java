package com.example.chinodelmundo.choicepicker;

/**
 * Created by Chino.DelMundo on 8/3/2017.
 */

public class QuestionDataModel {
    long id;
    String text;

    public QuestionDataModel(long id, String text) {
        this.text = text;
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public long getId() {
        return id;
    }
}
