import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HangmanGameGUI {
    private static final String WORDS_FILE_PATH = "D:\\EZTraining\\words.txt";

    private List<String> wordsList;
    private String wordToGuess;
    private StringBuilder guessedWord;
    private int triesLeft;
    private StringBuilder wrongGuesses;

    private JFrame frame;
    private HangmanDrawingPanel hangmanPanel;
    private JPanel textPanel;
    private JLabel wordLabel;
    private JLabel wrongGuessesLabel;
    private JPanel buttonPanel;
    private JButton[] letterButtons;

    private int hangmanPartsDrawn = 0;

    public HangmanGameGUI() {
        frame = new JFrame("Hangman Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 600);

        // Read words from the file
        readWordsFromFile();

        textPanel = new JPanel();
        frame.add(textPanel, BorderLayout.SOUTH);

        hangmanPanel = new HangmanDrawingPanel();
        frame.add(hangmanPanel, BorderLayout.CENTER);

        chooseRandomWord();
        initializeGame();
        createUI();

        frame.setResizable(false);
        frame.setVisible(true);
    }

    private void readWordsFromFile() {
        wordsList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(WORDS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                wordsList.add(line.trim());
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void chooseRandomWord() {
        wordToGuess = wordsList.get((int) (Math.random() * wordsList.size()));
    }

    private void initializeGame() {
        guessedWord = new StringBuilder("_".repeat(wordToGuess.length()));
        triesLeft = 6;
        wrongGuesses = new StringBuilder();
        hangmanPartsDrawn = 0;
    }

    private void createUI() {
        textPanel.setLayout(new GridLayout(3, 1));

        wordLabel = new JLabel("Word: " + guessedWord.toString().replaceAll("", " ").trim());
        textPanel.add(wordLabel);

        wrongGuessesLabel = new JLabel("Wrong guesses: " + wrongGuesses.toString());
        textPanel.add(wrongGuessesLabel);

        buttonPanel = new JPanel(new GridLayout(3, 9));
        letterButtons = new JButton[26];
        for (char c = 'a'; c <= 'z'; c++) {
            JButton button = new JButton(String.valueOf(c));
            letterButtons[c - 'a'] = button;
            button.addActionListener(new LetterButtonListener(c));
            buttonPanel.add(button);
        }
        textPanel.add(buttonPanel);
    }

    private void updateUI() {
        wordLabel.setText("Word: " + guessedWord.toString().replaceAll("", " ").trim());
        wrongGuessesLabel.setText("Wrong guesses: " + wrongGuesses.toString());
    }

    private void resetGame() {
        chooseRandomWord();
        initializeGame();
        updateUI();
        hangmanPanel.repaint();
    }

    private class LetterButtonListener implements ActionListener {
        private char letter;

        public LetterButtonListener(char letter) {
            this.letter = letter;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (triesLeft > 0 && guessedWord.indexOf("_") != -1) {
                if (wordToGuess.contains(String.valueOf(letter))) {
                    for (int i = 0; i < wordToGuess.length(); i++) {
                        if (wordToGuess.charAt(i) == letter) {
                            guessedWord.setCharAt(i, letter);
                        }
                    }
                } else {
                    triesLeft--;
                    wrongGuesses.append(letter).append(" ");
                    hangmanPartsDrawn++;
                }

                updateUI();

                if (guessedWord.indexOf("_") == -1) {
                    JOptionPane.showMessageDialog(frame, "Congratulations! You guessed the word: " + wordToGuess);
                    resetGame();
                }

                hangmanPanel.repaint();
            }
        }
    }

    private class HangmanDrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = getWidth();
            int height = getHeight();
            int bodyParts = 6;

            g.setColor(Color.BLACK);

            g.drawLine(20, height - 40, width - 20, height - 40);
            g.drawLine(20, height - 40, 20, 20);
            g.drawLine(20, 20, width / 2, 20);
            g.drawLine(width / 2, 20, width / 2, 50);

            if (hangmanPartsDrawn > 0) {
                g.drawOval(width / 2 - 20, 50, 40, 40); // Head
            }
            if (hangmanPartsDrawn > 1) {
                g.drawLine(width / 2, 90, width / 2, 150); // Body
            }
            if (hangmanPartsDrawn > 2) {
                g.drawLine(width / 2, 150, width / 2 - 20, 190); // Left Leg
            }
            if (hangmanPartsDrawn > 3) {
                g.drawLine(width / 2, 150, width / 2 + 20, 190); // Right Leg
            }
            if (hangmanPartsDrawn > 4) {
                g.drawLine(width / 2, 90, width / 2 - 20, 120); // Left Arm
            }
            if (hangmanPartsDrawn > 5) {
                g.drawLine(width / 2, 90, width / 2 + 20, 120); // Right Arm
            }

            if (hangmanPartsDrawn > bodyParts - 1) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(frame, "You ran out of tries! The word was: " + wordToGuess);
                    resetGame();
                });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HangmanGameGUI());
    }
}
