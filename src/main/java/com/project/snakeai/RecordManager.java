package com.project.snakeai;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RecordManager {
    private static final String FILE_PATH = "scores.txt";

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
            System.out.println("Saving records: " + records);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void saveRecord(GameRecord gameRecord) {
        List<GameRecord> records = loadRecords();
        records.add(gameRecord);
        saveRecords(records);
    }

    public static int getHighScore(List<GameRecord> records) {
        int highScore = 0;
        for (GameRecord record : records) {
            for (TryRecord tryRecord : record.getTries()) {
                highScore = Math.max(highScore, tryRecord.getScore());
                System.out.println(highScore+" "+tryRecord.getScore());
            }
        }
        return highScore;
    }
}  