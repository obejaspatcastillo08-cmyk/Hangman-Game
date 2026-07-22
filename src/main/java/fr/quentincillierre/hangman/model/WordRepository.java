package fr.quentincillierre.hangman.model;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WordRepository {

    private final Map<String, String> fruitClues = new HashMap<>();

    private final List<String> fruits = new ArrayList<>();

    private final Random random = new Random();

    public WordRepository() {
        try {
            InputStream input = getClass().getResourceAsStream("/words.txt");

            if (input == null) {
                throw new RuntimeException("Could not find words.txt");
            }

            Scanner scanner = new Scanner(input);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(":", 2);

                if (parts.length == 2) {
                    String word = parts[0].trim().toUpperCase();
                    String clue = parts[1].trim();

                    fruitClues.put(word, clue);
                    fruits.add(word);
                }
            }

            scanner.close();

        } catch (Exception e) {
            System.err.println("Error loading words.txt");
            e.printStackTrace();
        }
    }

    public String getRandomWord() {
        if (fruits.isEmpty()) {
            return "";
        }
        return fruits.get(random.nextInt(fruits.size()));
    }

    public String getClueForWord(String word) {
        return fruitClues.getOrDefault(word.toUpperCase(), "No clue available.");
    }
}