package com.project.snakeai;

import java.awt.Point;

public class Obstacle implements Comparable<Obstacle> {
    private Point position;
    private int priority;

    public Obstacle(Point position, int priority) {
        this.position = position;
        this.priority = priority;
    }

    public Point getPosition() {
        return position;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(Obstacle other) {
        return Integer.compare(this.priority, other.priority);
    }
}
