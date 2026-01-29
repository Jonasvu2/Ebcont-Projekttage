import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;

/**
 * Animal Memory Game - Ein Memory-Spiel mit Tier-Emojis
 * Finde alle passenden Tierpaare!
 */
public class AnimalMemoryGame extends JFrame {

    // Tier-Emojis für das Spiel - erweiterte Liste mit 32 Tieren!
    private static final String[] ANIMALS = {
            "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼",
            "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🐔",
            "🐧", "🦆", "🦅", "🦉", "🦋", "🐢", "🐍", "🦖",
            "🦈", "🐙", "🦀", "🐝", "🦄", "🐲", "🦩", "🦜"
    };

    // Bunte Farben für jedes Tier
    private static final Map<String, Color> ANIMAL_COLORS = new HashMap<>() {
        {
            put("🐶", new Color(255, 193, 128)); // Warmes Orange
            put("🐱", new Color(255, 182, 193)); // Rosa
            put("🐭", new Color(200, 200, 200)); // Grau
            put("🐹", new Color(255, 218, 185)); // Pfirsich
            put("🐰", new Color(255, 192, 203)); // Hell Rosa
            put("🦊", new Color(255, 140, 0)); // Dunkel Orange
            put("🐻", new Color(205, 133, 63)); // Braun
            put("🐼", new Color(230, 230, 250)); // Lavendel
            put("🐨", new Color(169, 169, 169)); // Dunkel Grau
            put("🐯", new Color(255, 165, 0)); // Orange
            put("🦁", new Color(255, 215, 0)); // Gold
            put("🐮", new Color(245, 245, 220)); // Beige
            put("🐷", new Color(255, 182, 193)); // Hell Rosa
            put("🐸", new Color(144, 238, 144)); // Hell Grün
            put("🐵", new Color(210, 180, 140)); // Tan
            put("🐔", new Color(255, 250, 205)); // Lemon
            put("🐧", new Color(135, 206, 235)); // Himmelblau
            put("🦆", new Color(100, 149, 237)); // Kornblumenblau
            put("🦅", new Color(139, 90, 43)); // Sattelbraun
            put("🦉", new Color(160, 82, 45)); // Sienna
            put("🦋", new Color(147, 112, 219)); // Medium Lila
            put("🐢", new Color(60, 179, 113)); // Medium Seegrün
            put("🐍", new Color(50, 205, 50)); // Limetten Grün
            put("🦖", new Color(102, 205, 170)); // Aquamarin
            // Neue Tiere
            put("🦈", new Color(70, 130, 180)); // Stahlblau (Hai)
            put("🐙", new Color(255, 105, 180)); // Hot Pink (Oktopus)
            put("🦀", new Color(255, 69, 0)); // Rot-Orange (Krabbe)
            put("🐝", new Color(255, 223, 0)); // Gelb (Biene)
            put("🦄", new Color(238, 130, 238)); // Violett (Einhorn)
            put("🐲", new Color(50, 205, 50)); // Limetten Grün (Drache)
            put("🦩", new Color(255, 20, 147)); // Deep Pink (Flamingo)
            put("🦜", new Color(0, 255, 127)); // Spring Grün (Papagei)
        }
    };

    private JButton[] cards;
    private String[] cardValues;
    private boolean[] cardFlipped;
    private boolean[] cardMatched;
    private int firstCardIndex = -1;
    private int secondCardIndex = -1;
    private int pairsFound = 0;
    private int moves = 0;
    private int gridSize = 4; // 4x4 = 16 Karten (8 Paare)
    private JLabel statusLabel;
    private JLabel movesLabel;
    private JPanel cardPanel;
    private Timer flipBackTimer;
    private boolean isProcessing = false;

    // Farben für das Design
    private static final Color BACKGROUND_COLOR = new Color(45, 52, 70);
    private static final Color CARD_BACK_COLOR = new Color(99, 102, 241);
    private static final Color CARD_FRONT_COLOR = new Color(255, 255, 255);
    private static final Color MATCHED_COLOR = new Color(34, 197, 94);
    private static final Color HEADER_COLOR = new Color(30, 35, 50);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color ACCENT_COLOR = new Color(251, 191, 36);

    public AnimalMemoryGame() {
        setTitle("🎮 Tier Memory Spiel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initializeUI();
        startNewGame();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeUI() {
        // Hauptpanel mit BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Karten Panel
        cardPanel = new JPanel(new GridLayout(gridSize, gridSize, 10, 10));
        cardPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // Footer Panel mit Buttons
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        // Titel
        JLabel titleLabel = new JLabel("🐾 Tier Memory 🐾");
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Status Panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        statsPanel.setBackground(HEADER_COLOR);

        movesLabel = new JLabel("Züge: 0");
        movesLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        movesLabel.setForeground(TEXT_COLOR);

        statusLabel = new JLabel("Finde alle Paare!");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statusLabel.setForeground(TEXT_COLOR);

        statsPanel.add(movesLabel);
        statsPanel.add(statusLabel);

        // Home Button
        JButton homeBtn = new JButton("🏠 Main Menu");
        homeBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        homeBtn.setBackground(ACCENT_COLOR);
        homeBtn.setForeground(Color.BLACK);
        homeBtn.setFocusPainted(false);
        homeBtn.setFocusable(false);
        homeBtn.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        homeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        homeBtn.addActionListener(e -> dispose());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        leftPanel.add(homeBtn);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(statsPanel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(BACKGROUND_COLOR);

        // Neues Spiel Button
        JButton newGameButton = createStyledButton("🔄 Neues Spiel", new Color(59, 130, 246));
        newGameButton.addActionListener(e -> startNewGame());

        // Schwierigkeit Buttons
        JButton easyButton = createStyledButton("Leicht (4x4)", new Color(34, 197, 94));
        easyButton.addActionListener(e -> changeDifficulty(4));

        JButton mediumButton = createStyledButton("Mittel (4x5)", new Color(251, 191, 36));
        mediumButton.addActionListener(e -> changeDifficulty(5));

        JButton hardButton = createStyledButton("Schwer (6x6)", new Color(239, 68, 68));
        hardButton.addActionListener(e -> changeDifficulty(6));

        JButton extremeButton = createStyledButton("Extrem (8x8)", new Color(139, 0, 139));
        extremeButton.addActionListener(e -> changeDifficulty(8));

        footerPanel.add(newGameButton);
        footerPanel.add(easyButton);
        footerPanel.add(mediumButton);
        footerPanel.add(hardButton);
        footerPanel.add(extremeButton);

        return footerPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Hover Effekt
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void changeDifficulty(int newSize) {
        if (newSize == 5) {
            // Für 4x5 Grid
            gridSize = 5;
            cardPanel.setLayout(new GridLayout(4, 5, 10, 10));
        } else {
            gridSize = newSize;
            cardPanel.setLayout(new GridLayout(gridSize, gridSize, 10, 10));
        }
        startNewGame();
        pack();
        setLocationRelativeTo(null);
    }

    private void startNewGame() {
        // Reset Variablen
        firstCardIndex = -1;
        secondCardIndex = -1;
        pairsFound = 0;
        moves = 0;
        isProcessing = false;

        movesLabel.setText("Züge: 0");
        statusLabel.setText("Finde alle Paare!");

        // Berechne Anzahl der Karten
        int totalCards = (gridSize == 5) ? 20 : gridSize * gridSize;
        int numPairs = totalCards / 2;

        // Initialisiere Arrays
        cards = new JButton[totalCards];
        cardValues = new String[totalCards];
        cardFlipped = new boolean[totalCards];
        cardMatched = new boolean[totalCards];

        // Wähle zufällige Tiere
        ArrayList<String> selectedAnimals = new ArrayList<>();
        ArrayList<String> animalList = new ArrayList<>(Arrays.asList(ANIMALS));
        Collections.shuffle(animalList);

        for (int i = 0; i < numPairs && i < animalList.size(); i++) {
            selectedAnimals.add(animalList.get(i));
            selectedAnimals.add(animalList.get(i)); // Jedes Tier zweimal (Paar)
        }

        // Mische die Karten
        Collections.shuffle(selectedAnimals);

        // Erstelle Karten-Buttons
        cardPanel.removeAll();

        for (int i = 0; i < totalCards; i++) {
            cardValues[i] = selectedAnimals.get(i);
            cardFlipped[i] = false;
            cardMatched[i] = false;

            final int index = i;
            cards[i] = createCardButton(index);
            cardPanel.add(cards[i]);
        }

        cardPanel.revalidate();
        cardPanel.repaint();
    }

    private JButton createCardButton(int index) {
        JButton card = new JButton("❓");
        card.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        card.setPreferredSize(new Dimension(90, 90));
        card.setBackground(CARD_BACK_COLOR);
        card.setForeground(Color.WHITE);
        card.setFocusPainted(false);
        card.setBorderPainted(false);
        card.setOpaque(true);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Abgerundete Ecken simulieren
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BACK_COLOR.darker(), 3),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        card.addActionListener(e -> handleCardClick(index));

        // Hover Effekt
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!cardFlipped[index] && !cardMatched[index] && !isProcessing) {
                    card.setBackground(CARD_BACK_COLOR.brighter());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!cardFlipped[index] && !cardMatched[index]) {
                    card.setBackground(CARD_BACK_COLOR);
                }
            }
        });

        return card;
    }

    private void handleCardClick(int index) {
        // Ignoriere Klicks während der Verarbeitung oder auf bereits aufgedeckte Karten
        if (isProcessing || cardFlipped[index] || cardMatched[index]) {
            return;
        }

        // Karte aufdecken
        flipCard(index, true);

        if (firstCardIndex == -1) {
            // Erste Karte ausgewählt
            firstCardIndex = index;
        } else if (secondCardIndex == -1 && index != firstCardIndex) {
            // Zweite Karte ausgewählt
            secondCardIndex = index;
            moves++;
            movesLabel.setText("Züge: " + moves);

            // Prüfe auf Match
            checkForMatch();
        }
    }

    private void flipCard(int index, boolean faceUp) {
        cardFlipped[index] = faceUp;

        if (faceUp) {
            String animal = cardValues[index];
            Color animalColor = ANIMAL_COLORS.getOrDefault(animal, CARD_FRONT_COLOR);
            cards[index].setText(animal);
            cards[index].setBackground(animalColor);
            cards[index].setForeground(Color.BLACK);
            cards[index].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(animalColor.darker(), 4),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        } else {
            cards[index].setText("❓");
            cards[index].setBackground(CARD_BACK_COLOR);
            cards[index].setForeground(Color.WHITE);
            cards[index].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(CARD_BACK_COLOR.darker(), 3),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        }
    }

    private void checkForMatch() {
        isProcessing = true;

        if (cardValues[firstCardIndex].equals(cardValues[secondCardIndex])) {
            // Match gefunden!
            cardMatched[firstCardIndex] = true;
            cardMatched[secondCardIndex] = true;
            pairsFound++;

            // Matched Card Style
            setMatchedStyle(firstCardIndex);
            setMatchedStyle(secondCardIndex);

            statusLabel.setText("🎉 Paar gefunden! " + pairsFound + "/" + (cards.length / 2));

            // Check auf Gewinn
            if (pairsFound == cards.length / 2) {
                showWinMessage();
            }

            resetTurn();
        } else {
            // Kein Match - Karten nach kurzer Verzögerung wieder umdrehen
            statusLabel.setText("❌ Kein Paar! Versuch es nochmal...");

            flipBackTimer = new Timer();
            flipBackTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        flipCard(firstCardIndex, false);
                        flipCard(secondCardIndex, false);
                        resetTurn();
                    });
                }
            }, 1000);
        }
    }

    private void setMatchedStyle(int index) {
        cards[index].setBackground(MATCHED_COLOR);
        cards[index].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MATCHED_COLOR.darker(), 3),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }

    private void resetTurn() {
        firstCardIndex = -1;
        secondCardIndex = -1;
        isProcessing = false;
    }

    private void showWinMessage() {
        String message = String.format(
                "🎊 Herzlichen Glückwunsch! 🎊\n\n" +
                        "Du hast alle Paare gefunden!\n" +
                        "Benötigte Züge: %d\n\n" +
                        "Möchtest du nochmal spielen?",
                moves);

        int choice = JOptionPane.showConfirmDialog(
                this,
                message,
                "🏆 Gewonnen!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            startNewGame();
        }
    }

    public static void main(String[] args) {
        // Setze Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Starte das Spiel im EDT
        SwingUtilities.invokeLater(() -> {
            new AnimalMemoryGame();
        });
    }
}
