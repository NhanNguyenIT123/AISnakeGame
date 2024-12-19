package com.project.snakeai;


import java.io.Serializable;

public class TryRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int score;

    public TryRecord(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}