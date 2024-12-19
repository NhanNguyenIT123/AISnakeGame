package com.project.snakeai;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class GameRecord implements Serializable {
    private final int width;
    private final int height;
    private final List<TryRecord> tries;

    public GameRecord(int width, int height) {
        this.width = width;
        this.height = height;
        this.tries = new ArrayList<>();
    }

    public void addTry(TryRecord tryRecord) {
        tries.add(tryRecord);
    }

    public List<TryRecord> getTries() {
        return tries;
    }
}
