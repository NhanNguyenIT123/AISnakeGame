package com.project.snakeai;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class RecordManager {
    private static final String FILE_PATH = "scores.txt";
    private static final int MAX_TOP_SCORES = 10;
    private static PriorityQueue<Integer> topScores = new PriorityQueue<>(MAX_TOP_SCORES);

    public static List<GameRecord> loadRecords() {
        List<GameRecord> records = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            records = (List<GameRecord>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No existing records found.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return records;
    }

    public static void saveRecords(List<GameRecord> records) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(records);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void saveRecord(GameRecord gameRecord) {
        List<GameRecord> records = loadRecords();
        records.add(gameRecord);
        saveRecords(records);
    }

    public static List<Integer> getScoresFromRecords() {
        List<GameRecord> records = loadRecords();
        List<Integer> scores = new ArrayList<>();

        for (GameRecord record : records) {
            for (TryRecord tryRecord : record.getTries()) {
                scores.add(tryRecord.getScore());
            }
        }

        return scores;
    }

    public static void addScoreToHeap(int score) {
        if (topScores.size() < MAX_TOP_SCORES) {
            topScores.add(score);
        } else if (score > topScores.peek()) {
            topScores.poll();
            topScores.add(score); 
        }
    }

    public static List<Integer> getTopScores() {
        List<Integer> scores = getScoresFromRecords();
        quickSort(scores, 0, scores.size() - 1);
        return scores;
    }

    private static void quickSort(List<Integer> scores, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(scores, low, high);
            quickSort(scores, low, pivotIndex - 1);
            quickSort(scores, pivotIndex + 1, high);
        }
    }
    
    private static int partition(List<Integer> scores, int low, int high) {
        int pivot = scores.get(high); 
        int i = low - 1;
    
        for (int j = low; j < high; j++) {
            if (scores.get(j) > pivot) { 
                i++;
                int temp = scores.get(i);
                scores.set(i, scores.get(j));
                scores.set(j, temp);
            }
        }
        int temp = scores.get(i + 1);
        scores.set(i + 1, scores.get(high));
        scores.set(high, temp);
    
        return i + 1;
    }


}  