package com.project.snakeai;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SnakeGameApp {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game AI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);  
            frame.setResizable(false);

            MainMenuPanel mainMenu = new MainMenuPanel(frame);
            frame.add(mainMenu);

            frame.setVisible(true);
        });
    }
}

class MainMenuPanel extends JPanel {
    public MainMenuPanel(JFrame parentFrame) {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Snake Game AI", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        add(title, BorderLayout.NORTH);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton playButton = new JButton("Play");
        playButton.setFont(new Font("Arial", Font.PLAIN, 24));
        playButton.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.add(new PlayOptionsPanel(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        JButton recordButton = new JButton("Record");
        recordButton.setFont(new Font("Arial", Font.PLAIN, 24));
        recordButton.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.add(new RecordPanel(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 24));
        exitButton.addActionListener(e -> System.exit(0));

        buttonsPanel.add(playButton);
        buttonsPanel.add(recordButton);
        buttonsPanel.add(exitButton);

        add(buttonsPanel, BorderLayout.CENTER);
    }
}

class PlayOptionsPanel extends JPanel {
    public PlayOptionsPanel(JFrame parentFrame) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Choose Grid Size", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel widthLabel = new JLabel("Width:");
        JTextField widthField = new JTextField("30");

        JLabel heightLabel = new JLabel("Height:");
        JTextField heightField = new JTextField("20");

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.PLAIN, 24));
        startButton.addActionListener(e -> {
            int width = Integer.parseInt(widthField.getText());
            int height = Integer.parseInt(heightField.getText());
            GameRecord gameRecord = new GameRecord(width, height);
            SnakeGameAI game = new SnakeGameAI(width, height, gameRecord, parentFrame);

            int frameWidth = width * SnakeGameAI.TILE_SIZE;
            int frameHeight = height * SnakeGameAI.TILE_SIZE;
            parentFrame.setSize(frameWidth + 20, frameHeight + 40);

            parentFrame.getContentPane().removeAll();
            parentFrame.add(game);
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        optionsPanel.add(widthLabel);
        optionsPanel.add(widthField);
        optionsPanel.add(heightLabel);
        optionsPanel.add(heightField);
        optionsPanel.add(startButton);

        add(optionsPanel, BorderLayout.CENTER);
    }
}

class RecordPanel extends JPanel {
    private List<GameRecord> records;

    public RecordPanel(JFrame parentFrame) {
    	records = RecordManager.loadRecords();
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Game Records", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        JTextArea recordArea = new JTextArea();
        recordArea.setEditable(false);
        recordArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        updateRecordArea(recordArea);

        JScrollPane scrollPane = new JScrollPane(recordArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Main Menu");
        backButton.setFont(new Font("Arial", Font.PLAIN, 24));
        backButton.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.add(new MainMenuPanel(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        add(backButton, BorderLayout.SOUTH);
    }

    private void updateRecordArea(JTextArea recordArea) {
        StringBuilder sb = new StringBuilder();
        sb.append("High Score: ").append(getHighScore()).append("\n\n");
        if (records.isEmpty()) {
            sb.append("No records available.\n");
        }
        for (int i = 0; i < records.size(); i++) {
            GameRecord record = records.get(i);
            sb.append("Game Record ").append(i + 1).append(":\n");

            for (int j = 0; j < record.getTries().size(); j++) {
                TryRecord tryRecord = record.getTries().get(j);
                sb.append("\tAttempt ").append(j + 1).append(": Score = ")
                        .append(tryRecord.getScore()).append("\n");
            }
        }
        recordArea.setText(sb.toString());
    }

    private int getHighScore() {
        int highScore = 0;
        for (GameRecord record : records) {
            for (TryRecord tryRecord : record.getTries()) {
                highScore = Math.max(highScore, tryRecord.getScore());
            }
        }
        return highScore;
    }
}

