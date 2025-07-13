package com.example.quizzapp.models;

import java.io.Serializable;

public class Option implements Serializable {
    private String letter;
    private String text;
    private boolean isCorrect;
    private String _id;

    public String getLetter() {
        return letter;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public String getId() {
        return _id;
    }
}
