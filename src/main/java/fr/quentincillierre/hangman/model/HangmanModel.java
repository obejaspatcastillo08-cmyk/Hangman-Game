package fr.quentincillierre.hangman.model;

import java.util.HashSet;
import java.util.Set;

public class HangmanModel {

    private final String wordToGuess;
    private final int maxWrongs = 10; // Classic hangman is usually 8 attempts
    private int currentWrongs;
    private final Set<Character> guessedLetters;

    public HangmanModel(String wordToGuess) {
        this.wordToGuess = wordToGuess.toUpperCase();
        this.currentWrongs = 0;
        this.guessedLetters = new HashSet<>();
    }

    public String getWordToGuess() {
        return this.wordToGuess;
    }

    public int getCurrentWrongs() {
        return this.currentWrongs;
    }

    public Set<Character> getGuessedLetter() {
        return this.guessedLetters;
    }

    public void tryLetter(Character letter) {
        if (letter == null) {
            return;
        }

        char upperLetter = Character.toUpperCase(letter);

        if (guessedLetters.contains(upperLetter) || isWin() || isLose()) {
            return;
        }

        guessedLetters.add(upperLetter);

        if (wordToGuess.indexOf(upperLetter) == -1) {
            currentWrongs++;
        }
    }

    public String getHiddenWord() {
        StringBuilder hidden = new StringBuilder();
        
        for (int i = 0; i < wordToGuess.length(); i++) {
            char c = wordToGuess.charAt(i);
            if (guessedLetters.contains(c)) {
                hidden.append(c);
            } else {
                hidden.append('_');
            }
        }
        return hidden.toString();
    }

    public boolean isWin() {
    if (wordToGuess == null || wordToGuess.isEmpty()) {
        return false;
    }

    for (int i = 0; i < wordToGuess.length(); i++) {
        char c = wordToGuess.charAt(i);
        
        if (Character.isLetter(c)) {
            char upperChar = Character.toUpperCase(c);
            if (!guessedLetters.contains(upperChar)) {
                return false; 
            }
        }
    }
    
    return true;
}
    public boolean isLose() {
        return currentWrongs >= maxWrongs;
    }

    public static void main(String[] args) {
        HangmanModel game = new HangmanModel("java");
        game.tryLetter('R');
        System.out.println(game.getHiddenWord());
        game.tryLetter('A');
        System.out.println(game.getHiddenWord()); 
        System.out.println(game.isWin() ? "WIN" : "KEEP PUSHING"); 
        game.tryLetter('v');
        game.tryLetter('j');
        System.out.println(game.isWin() ? "WIN" : "KEEP PUSHING"); 
        
        // Let's test wrong guesses
        game.tryLetter('g');
        game.tryLetter('g');
        game.tryLetter('g');
        game.tryLetter('g');
        game.tryLetter('g');
        game.tryLetter('g');
        game.tryLetter('g');
        game.tryLetter('g');
        System.out.println(game.isLose() ? "LOSE" : "KEEP PUSHING"); 
        game.tryLetter('g');
        System.out.println(game.isLose() ? "LOSE" : "KEEP PUSHING"); 
    }
}