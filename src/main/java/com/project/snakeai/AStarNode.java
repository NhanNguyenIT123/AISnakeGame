package com.project.snakeai;

import java.awt.Point;

public class AStarNode implements Comparable<AStarNode> {
    private Point point;
    private int gScore; 
    private int fScore; 
    public AStarNode(Point point, int gScore, int fScore) {
        this.point = point;
        this.gScore = gScore;
        this.fScore = fScore;
    }

    public Point getPoint() {
        return point;
    }

    @Override
    public int compareTo(AStarNode other) {
        return Integer.compare(this.fScore, other.fScore); 
    }
}
