package com.project.snakeai;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.project.snakeai.Obstacle;
import com.project.snakeai.AStarNode;

public class SnakeGameAI extends JPanel implements ActionListener {
    public static final int TILE_SIZE = 20;
    private static final int GAME_SPEED = 100;

    private final int gridWidth;
    private final int gridHeight;
    private final LinkedList<Point> snake = new LinkedList<>();
    private Point food;
    private char direction = 'R';
    private boolean running = true;
    private Timer timer;
    private int score = 0;
    private int randomMoveCounter = 0;
    private PriorityQueue<Obstacle> obstacleQueue = new PriorityQueue<>();
    private List<Point> obstacles = new ArrayList<>();
    private GameRecord gameRecord;
    private List<GameRecord> records; 

    private final JFrame parentFrame;

    public SnakeGameAI(int gridWidth, int gridHeight, GameRecord gameRecord, JFrame parentFrame) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.gameRecord = gameRecord;
        this.parentFrame = parentFrame;

        setPreferredSize(new Dimension(gridWidth * TILE_SIZE, gridHeight * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocusInWindow();

        this.records = RecordManager.loadRecords();
        records.add(gameRecord); 
        RecordManager.saveRecords(records);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!running && e.getKeyCode() == KeyEvent.VK_R) {
                    restartGame();
                } else if (!running && e.getKeyCode() == KeyEvent.VK_B) {
                    returnToMenu();
                }
            }
        });

        initGame();
    }

    private void initGame() {
        snake.clear();
        snake.add(new Point(5, 5));
        direction = 'R';
        score = 0;
        spawnFood();
        running = true;

        timer = new Timer(GAME_SPEED, this);
        timer.start();
    }

    private void restartGame() {
        saveScore();
        initGame();
        repaint();
    }

    private void saveScore() {
        gameRecord.addTry(new TryRecord(score)); 
        RecordManager.saveRecords(records);
        RecordManager.addScoreToHeap(score);
    }

    private void spawnFood() {
        Random random = new Random();
        do {
            int x = random.nextInt(gridWidth);
            int y = random.nextInt(gridHeight);
            food = new Point(x, y);
        } while (snake.contains(food));
    }

    private boolean moveSnake() {
        Point head = snake.getFirst();
        Point nextHead;

        switch (direction) {
            case 'U': nextHead = new Point(head.x, head.y - 1); break;
            case 'D': nextHead = new Point(head.x, head.y + 1); break;
            case 'L': nextHead = new Point(head.x - 1, head.y); break;
            case 'R': nextHead = new Point(head.x + 1, head.y); break;
            default: throw new IllegalStateException("Unexpected direction: " + direction);
        }

        if (nextHead.x < 0 || nextHead.y < 0 || nextHead.x >= gridWidth || nextHead.y >= gridHeight || snake.contains(nextHead)) {
            running = false;
            timer.stop();
            saveScore();
            return false;
        }

        snake.addFirst(nextHead);

        if (nextHead.equals(food)) {
            score += 10;
            spawnFood();
        } else {
            snake.removeLast();
        }

        return true;
    }

    private boolean bfsFindPath() {
        Point start = snake.getFirst();
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> parentMap = new HashMap<>();
        Set<Point> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.equals(food)) {
                LinkedList<Point> path = new LinkedList<>();
                for (Point p = food; p != null; p = parentMap.get(p)) {
                    path.addFirst(p);
                }

                if (path.size() > 1) {
                    Point nextStep = path.get(1);
                    if (nextStep.x > start.x) direction = 'R';
                    else if (nextStep.x < start.x) direction = 'L';
                    else if (nextStep.y > start.y) direction = 'D';
                    else if (nextStep.y < start.y) direction = 'U';
                }
                return true;
            }

            for (Point neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor) && !snake.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    parentMap.put(neighbor, current);
                }
            }
        }

        return false;
    }

    private List<Point> getNeighbors(Point p) {
        List<Point> neighbors = new ArrayList<>();
        if (p.x > 0) neighbors.add(new Point(p.x - 1, p.y));
        if (p.x < gridWidth - 1) neighbors.add(new Point(p.x + 1, p.y));
        if (p.y > 0) neighbors.add(new Point(p.x, p.y - 1));
        if (p.y < gridHeight - 1) neighbors.add(new Point(p.x, p.y + 1));
        return neighbors;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            if (!bfsFindPath()) {
                if (randomMoveCounter % 5 == 0) {
                    bfsFindPath();
                }
                moveRandomlyAvoidingBody();
                randomMoveCounter++;
            } else {
                moveSnake();
            }
            if (score % 50 == 0) { 
                generateObstacles();
            }
    
            spawnObstacle();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (running) {
            g.setColor(Color.RED);
            g.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

            g.setColor(Color.GREEN);
            for (Point p : snake) {
                g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }

            g.setColor(Color.GRAY);
            for (Point obstacle : obstacles) {
                g.fillRect(obstacle.x * TILE_SIZE, obstacle.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }

            g.setColor(Color.WHITE);
            g.drawString("Score: " + score, 10, 20);
        } else {
            g.setColor(Color.RED);
            int fontSize = Math.max(36, gridHeight / 5);
            g.setFont(new Font("Arial", Font.BOLD, fontSize));
            String message1 = "Game Over! Press 'R' to Restart";
            String message2 = "Press 'B' to Return to Menu";
            FontMetrics fm = g.getFontMetrics();
            int x1 = (getWidth() - fm.stringWidth(message1)) / 2;
            int y1 = getHeight() / 2 - 20;
            int x2 = (getWidth() - fm.stringWidth(message2)) / 2;
            int y2 = y1 + 40;

            g.drawString(message1, x1, y1);
            g.drawString(message2, x2, y2);
        }
    }
    
    private void returnToMenu() {
        timer.stop();
        parentFrame.getContentPane().removeAll();
        parentFrame.add(new MainMenuPanel(parentFrame));
        parentFrame.revalidate();
        parentFrame.repaint();
    }
    
    private void moveRandomlyAvoidingBody() {
        Point head = snake.getFirst();
        List<Character> possibleDirections = new ArrayList<>();

        Set<Point> body = new HashSet<>(snake);

        if (isSafeMove(new Point(head.x, head.y - 1), body)) possibleDirections.add('U'); 
        if (isSafeMove(new Point(head.x, head.y + 1), body)) possibleDirections.add('D'); 
        if (isSafeMove(new Point(head.x - 1, head.y), body)) possibleDirections.add('L'); 
        if (isSafeMove(new Point(head.x + 1, head.y), body)) possibleDirections.add('R'); 

        if (!possibleDirections.isEmpty()) {
            Random random = new Random();
            direction = possibleDirections.get(random.nextInt(possibleDirections.size()));
            moveSnake();
        } else {
            running = false;
            saveScore();
        }
    }
    
    private boolean isSafeMove(Point p, Set<Point> body) {
        return p.x >= 0 && p.y >= 0 && p.x < gridWidth && p.y < gridHeight && !body.contains(p);
    }

    private boolean isObstacle(Point p) {
        return obstacles.contains(p);
    }

    private int heuristic(Point start, Point end) {
        return Math.abs(start.x - end.x) + Math.abs(start.y - end.y);
    }

    private void spawnObstacle() {
        if (!obstacleQueue.isEmpty()) {
            Obstacle nextObstacle = obstacleQueue.poll();
            Point position = nextObstacle.getPosition();

            if (pathExists(snake.getFirst(), food)) {
                obstacles.add(position);
            }
        }
    }

    private void generateObstacles() {
        Random random = new Random();
        Point snakeHead = snake.getFirst();
    
        for (int i = 0; i < 5; i++) { 
            Point randomPoint;
            do {
                int x = random.nextInt(gridWidth);
                int y = random.nextInt(gridHeight);
                randomPoint = new Point(x, y);
            } while (snake.contains(randomPoint) || food.equals(randomPoint) || isObstacle(randomPoint));
    
            int priority = Math.abs(randomPoint.x - snakeHead.x) + Math.abs(randomPoint.y - snakeHead.y); 
            obstacleQueue.add(new Obstacle(randomPoint, priority));
        }
    }

    private boolean pathExists(Point start, Point end) {
        if (start.equals(end)) return true;
    
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>();
        Set<Point> closedSet = new HashSet<>();
        Map<Point, Integer> gScore = new HashMap<>();
    
        openSet.add(new AStarNode(start, 0, heuristic(start, end)));
        gScore.put(start, 0);
    
        while (!openSet.isEmpty()) {
            AStarNode currentNode = openSet.poll();
            Point current = currentNode.getPoint();
    
            if (current.equals(end)) {
                return true; 
            }
    
            closedSet.add(current);
    
            for (Point neighbor : getNeighbors(current)) {
                if (closedSet.contains(neighbor) || snake.contains(neighbor) || isObstacle(neighbor)) {
                    continue; 
                }
    
                int tentativeGScore = gScore.getOrDefault(current, Integer.MAX_VALUE) + 1;
    
                if (tentativeGScore < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    gScore.put(neighbor, tentativeGScore);
                    int fScore = tentativeGScore + heuristic(neighbor, end);
                    openSet.add(new AStarNode(neighbor, tentativeGScore, fScore));
                }
            }
        }
    
        return false; 
    }
    


}
