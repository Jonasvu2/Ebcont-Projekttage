import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Snake Game - Klassisches Schlangenspiel
 * Steuere die Schlange und sammle Äpfel!
 */
public class SnakeGame extends JFrame {

    public SnakeGame() {
        setTitle("🐍 Snake Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainContainer = new JPanel(new BorderLayout());
        SnakePanel snakePanel = new SnakePanel();

        // Home Button Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.BLACK);

        JButton homeBtn = new JButton("🏠 Main Menu");
        homeBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        homeBtn.setBackground(new Color(18, 185, 129)); // Snake Green
        homeBtn.setForeground(Color.BLACK);
        homeBtn.setFocusPainted(false);
        homeBtn.setFocusable(false);
        homeBtn.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        homeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        homeBtn.addActionListener(e -> dispose());

        headerPanel.add(homeBtn);

        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(snakePanel, BorderLayout.CENTER);

        add(mainContainer);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new SnakeGame());
    }
}

class SnakePanel extends JPanel implements ActionListener {

    // Spielfeld Einstellungen
    private static final int TILE_SIZE = 25;
    private static final int GRID_WIDTH = 24;
    private static final int GRID_HEIGHT = 20;
    private static final int SCREEN_WIDTH = TILE_SIZE * GRID_WIDTH;
    private static final int SCREEN_HEIGHT = TILE_SIZE * GRID_HEIGHT;
    private static final int DELAY = 120; // Geschwindigkeit in ms (Niedriger = Schneller)

    // Farben
    private static final Color BACKGROUND_COLOR = Color.BLACK;
    private static final Color GRID_COLOR = new Color(30, 10, 10);
    private static final Color SNAKE_HEAD_COLOR = new Color(180, 0, 0);
    private static final Color SNAKE_BODY_COLOR = new Color(130, 0, 0);
    private static final Color SNAKE_BODY_ALT_COLOR = new Color(110, 0, 0);
    private static final Color APPLE_COLOR = new Color(255, 215, 0);
    private static final Color APPLE_HIGHLIGHT = new Color(255, 255, 150);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color SCORE_BG_COLOR = new Color(10, 10, 10);
    private static final Color OBSTACLE_COLOR = new Color(100, 100, 110);
    private static final Color OBSTACLE_BORDER = new Color(60, 60, 70);

    // Schlange
    private ArrayList<Point> snake;
    private int direction; // 0=hoch, 1=rechts, 2=runter, 3=links
    private int nextDirection;

    // Apfel
    private Point apple;

    // Hindernisse
    private ArrayList<Point> obstacles;

    // Spielstatus
    private boolean running;
    private boolean gameOver;
    private int score;
    private int highScore;
    private javax.swing.Timer timer;

    // Richtungskonstanten
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;

    public SnakePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT + 50));
        setBackground(BACKGROUND_COLOR);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        startNewGame();
    }

    private void startNewGame() {
        snake = new ArrayList<>();

        // Starte in der Mitte
        int startX = GRID_WIDTH / 2;
        int startY = GRID_HEIGHT / 2;

        snake.add(new Point(startX, startY));
        snake.add(new Point(startX - 1, startY));
        snake.add(new Point(startX - 2, startY));

        direction = RIGHT;
        nextDirection = RIGHT;

        spawnObstacles();
        spawnApple();

        score = 0;
        running = true;
        gameOver = false;

        if (timer != null) {
            timer.stop();
        }
        timer = new javax.swing.Timer(DELAY, this);
        timer.start();
    }

    private void spawnApple() {
        Random random = new Random();
        boolean validPosition;

        do {
            int x = random.nextInt(GRID_WIDTH);
            int y = random.nextInt(GRID_HEIGHT);
            apple = new Point(x, y);

            validPosition = true;
            // Nicht in der Schlange
            for (Point segment : snake) {
                if (segment.equals(apple)) {
                    validPosition = false;
                    break;
                }
            }
            // Nicht in Hindernissen
            if (validPosition) {
                for (Point obs : obstacles) {
                    if (obs.equals(apple)) {
                        validPosition = false;
                        break;
                    }
                }
            }
        } while (!validPosition);
    }

    private void spawnObstacles() {
        obstacles = new ArrayList<>();
        Random random = new Random();
        int numObstacles = 13; // Genau 13 Hindernisse

        for (int i = 0; i < numObstacles; i++) {
            boolean valid;
            Point p;
            do {
                valid = true;
                p = new Point(random.nextInt(GRID_WIDTH), random.nextInt(GRID_HEIGHT));

                // Nicht im Startbereich der Schlange
                if (Math.abs(p.x - GRID_WIDTH / 2) < 5 && Math.abs(p.y - GRID_HEIGHT / 2) < 2) {
                    valid = false;
                }

                // Nicht doppelt
                for (Point existing : obstacles) {
                    if (existing.equals(p)) {
                        valid = false;
                        break;
                    }
                }
            } while (!valid);
            obstacles.add(p);
        }
    }

    private void handleKeyPress(KeyEvent e) {
        int key = e.getKeyCode();

        if (gameOver) {
            if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_ENTER) {
                startNewGame();
            }
            return;
        }

        switch (key) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                if (direction != DOWN)
                    nextDirection = UP;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                if (direction != LEFT)
                    nextDirection = RIGHT;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                if (direction != UP)
                    nextDirection = DOWN;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                if (direction != RIGHT)
                    nextDirection = LEFT;
                break;
            case KeyEvent.VK_SPACE:
                if (!running && !gameOver) {
                    startNewGame();
                }
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            direction = nextDirection;
            move();
            checkApple();
            checkCollision();
        }
        repaint();
    }

    private void move() {
        Point head = snake.get(0);
        Point newHead = new Point(head);

        switch (direction) {
            case UP:
                newHead.y--;
                break;
            case RIGHT:
                newHead.x++;
                break;
            case DOWN:
                newHead.y++;
                break;
            case LEFT:
                newHead.x--;
                break;
        }

        snake.add(0, newHead);
        snake.remove(snake.size() - 1);
    }

    private void checkApple() {
        Point head = snake.get(0);

        if (head.equals(apple)) {
            // Schlange wächst
            Point tail = snake.get(snake.size() - 1);
            snake.add(new Point(tail));

            score += 10;
            if (score > highScore) {
                highScore = score;
            }

            spawnApple();

            // Spiel wird schneller
            int newDelay = Math.max(50, DELAY - (score / 50) * 5);
            timer.setDelay(newDelay);
        }
    }

    private void checkCollision() {
        Point head = snake.get(0);

        // Wand Kollision
        if (head.x < 0 || head.x >= GRID_WIDTH || head.y < 0 || head.y >= GRID_HEIGHT) {
            gameOver = true;
            running = false;
            return;
        }

        // Selbst Kollision
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver = true;
                running = false;
                return;
            }
        }

        // Hindernis Kollision
        for (Point obs : obstacles) {
            if (head.equals(obs)) {
                gameOver = true;
                running = false;
                return;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g2d);
        drawObstacles(g2d);
        drawApple(g2d);
        drawSnake(g2d);
        drawScore(g2d);

        if (gameOver) {
            drawGameOver(g2d);
        }
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(GRID_COLOR);
        for (int x = 0; x <= GRID_WIDTH; x++) {
            g.drawLine(x * TILE_SIZE, 0, x * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
        }
        for (int y = 0; y <= GRID_HEIGHT; y++) {
            g.drawLine(0, y * TILE_SIZE, SCREEN_WIDTH, y * TILE_SIZE);
        }
    }

    private void drawObstacles(Graphics2D g) {
        for (Point obs : obstacles) {
            int x = obs.x * TILE_SIZE;
            int y = obs.y * TILE_SIZE;

            // Stein-Design
            g.setColor(OBSTACLE_BORDER);
            g.fillRoundRect(x, y, TILE_SIZE, TILE_SIZE, 5, 5);
            g.setColor(OBSTACLE_COLOR);
            g.fillRoundRect(x + 3, y + 3, TILE_SIZE - 6, TILE_SIZE - 6, 4, 4);

            // Risse im Stein
            g.setColor(OBSTACLE_BORDER);
            g.drawLine(x + 5, y + 5, x + 12, y + 12);
            g.drawLine(x + 15, y + 5, x + 8, y + 15);
        }
    }

    private void drawSnake(Graphics2D g) {
        for (int i = 0; i < snake.size(); i++) {
            Point segment = snake.get(i);
            int x = segment.x * TILE_SIZE;
            int y = segment.y * TILE_SIZE;

            if (i == 0) {
                // Gruseliger Kopf (dunklerer Rand)
                g.setColor(new Color(100, 0, 0));
                g.fillRoundRect(x, y, TILE_SIZE, TILE_SIZE, 10, 10);
                g.setColor(SNAKE_HEAD_COLOR);
                g.fillRoundRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4, 8, 8);

                // Zunge (Flackernd)
                if (System.currentTimeMillis() % 400 < 200) {
                    g.setColor(new Color(255, 0, 0));
                    int tx = x + TILE_SIZE / 2;
                    int ty = y + TILE_SIZE / 2;
                    switch (direction) {
                        case UP:
                            g.fillRect(tx - 1, y - 8, 2, 8);
                            g.fillRect(tx - 3, y - 10, 2, 3);
                            g.fillRect(tx + 1, y - 10, 2, 3);
                            break;
                        case DOWN:
                            g.fillRect(tx - 1, y + TILE_SIZE, 2, 8);
                            g.fillRect(tx - 3, y + TILE_SIZE + 7, 2, 3);
                            g.fillRect(tx + 1, y + TILE_SIZE + 7, 2, 3);
                            break;
                        case LEFT:
                            g.fillRect(x - 8, ty - 1, 8, 2);
                            g.fillRect(x - 10, ty - 3, 3, 2);
                            g.fillRect(x - 10, ty + 1, 3, 2);
                            break;
                        case RIGHT:
                            g.fillRect(x + TILE_SIZE, ty - 1, 8, 2);
                            g.fillRect(x + TILE_SIZE + 7, ty - 3, 3, 2);
                            g.fillRect(x + TILE_SIZE + 7, ty + 1, 3, 2);
                            break;
                    }
                }

                // Glühende Augen (Gelb)
                g.setColor(new Color(255, 255, 0));
                int eyeSize = 6;
                int eyeOffset = 4;

                switch (direction) {
                    case UP:
                        g.fillOval(x + eyeOffset, y + 4, eyeSize, eyeSize);
                        g.fillOval(x + TILE_SIZE - eyeOffset - eyeSize, y + 4, eyeSize, eyeSize);
                        g.setColor(Color.BLACK); // Schlitzpupille
                        g.fillRect(x + eyeOffset + 2, y + 4, 2, eyeSize);
                        g.fillRect(x + TILE_SIZE - eyeOffset - eyeSize + 2, y + 4, 2, eyeSize);
                        break;
                    case RIGHT:
                        g.fillOval(x + TILE_SIZE - 10, y + eyeOffset, eyeSize, eyeSize);
                        g.fillOval(x + TILE_SIZE - 10, y + TILE_SIZE - eyeOffset - eyeSize, eyeSize, eyeSize);
                        g.setColor(Color.BLACK);
                        g.fillRect(x + TILE_SIZE - 10, y + eyeOffset + 2, eyeSize, 2);
                        g.fillRect(x + TILE_SIZE - 10, y + TILE_SIZE - eyeOffset - eyeSize + 2, eyeSize, 2);
                        break;
                    case DOWN:
                        g.fillOval(x + eyeOffset, y + TILE_SIZE - 10, eyeSize, eyeSize);
                        g.fillOval(x + TILE_SIZE - eyeOffset - eyeSize, y + TILE_SIZE - 10, eyeSize, eyeSize);
                        g.setColor(Color.BLACK);
                        g.fillRect(x + eyeOffset + 2, y + TILE_SIZE - 10, 2, eyeSize);
                        g.fillRect(x + TILE_SIZE - eyeOffset - eyeSize + 2, y + TILE_SIZE - 10, 2, eyeSize);
                        break;
                    case LEFT:
                        g.fillOval(x + 4, y + eyeOffset, eyeSize, eyeSize);
                        g.fillOval(x + 4, y + TILE_SIZE - eyeOffset - eyeSize, eyeSize, eyeSize);
                        g.setColor(Color.BLACK);
                        g.fillRect(x + 4, y + eyeOffset + 2, eyeSize, 2);
                        g.fillRect(x + 4, y + TILE_SIZE - eyeOffset - eyeSize + 2, eyeSize, 2);
                        break;
                }

                // Reißzähne
                g.setColor(Color.WHITE);
                switch (direction) {
                    case UP:
                        g.fillPolygon(new int[] { x + 6, x + 9, x + 7 }, new int[] { y + 2, y + 2, y + 7 }, 3);
                        g.fillPolygon(new int[] { x + TILE_SIZE - 6, x + TILE_SIZE - 9, x + TILE_SIZE - 7 },
                                new int[] { y + 2, y + 2, y + 7 }, 3);
                        break;
                    case DOWN:
                        g.fillPolygon(new int[] { x + 6, x + 9, x + 7 },
                                new int[] { y + TILE_SIZE - 2, y + TILE_SIZE - 2, y + TILE_SIZE - 7 }, 3);
                        g.fillPolygon(new int[] { x + TILE_SIZE - 6, x + TILE_SIZE - 9, x + TILE_SIZE - 7 },
                                new int[] { y + TILE_SIZE - 2, y + TILE_SIZE - 2, y + TILE_SIZE - 7 }, 3);
                        break;
                    case LEFT:
                        g.fillPolygon(new int[] { x + 2, x + 2, x + 7 }, new int[] { y + 6, y + 9, y + 7 }, 3);
                        g.fillPolygon(new int[] { x + 2, x + 2, x + 7 },
                                new int[] { y + TILE_SIZE - 6, y + TILE_SIZE - 9, y + TILE_SIZE - 7 }, 3);
                        break;
                    case RIGHT:
                        g.fillPolygon(new int[] { x + TILE_SIZE - 2, x + TILE_SIZE - 2, x + TILE_SIZE - 7 },
                                new int[] { y + 6, y + 9, y + 7 }, 3);
                        g.fillPolygon(new int[] { x + TILE_SIZE - 2, x + TILE_SIZE - 2, x + TILE_SIZE - 7 },
                                new int[] { y + TILE_SIZE - 6, y + TILE_SIZE - 9, y + TILE_SIZE - 7 }, 3);
                        break;
                }
            } else {
                // Körper mit "Venom"-Effekt (dunkle Flecken)
                if (i % 2 == 0) {
                    g.setColor(SNAKE_BODY_COLOR);
                } else {
                    g.setColor(SNAKE_BODY_ALT_COLOR);
                }
                g.fillRoundRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4, 6, 6);

                // Kleine dunkle Adern/Flecken
                g.setColor(new Color(60, 0, 0, 100));
                g.fillOval(x + 5, y + 5, 4, 4);
                g.fillOval(x + TILE_SIZE - 10, y + TILE_SIZE - 10, 3, 3);
            }
        }
    }

    private void drawApple(Graphics2D g) {
        int x = apple.x * TILE_SIZE;
        int y = apple.y * TILE_SIZE;

        // Apfel
        g.setColor(APPLE_COLOR);
        g.fillOval(x + 2, y + 4, TILE_SIZE - 4, TILE_SIZE - 6);

        // Highlight
        g.setColor(APPLE_HIGHLIGHT);
        g.fillOval(x + 5, y + 7, 6, 6);

        // Stiel
        g.setColor(new Color(139, 69, 19));
        g.fillRect(x + TILE_SIZE / 2 - 1, y + 2, 3, 5);

        // Blatt
        g.setColor(new Color(34, 139, 34));
        g.fillOval(x + TILE_SIZE / 2 + 2, y + 1, 6, 4);
    }

    private void drawScore(Graphics2D g) {
        // Score Hintergrund
        g.setColor(SCORE_BG_COLOR);
        g.fillRect(0, SCREEN_HEIGHT, SCREEN_WIDTH, 50);

        // Trennlinie
        g.setColor(SNAKE_HEAD_COLOR);
        g.fillRect(0, SCREEN_HEIGHT, SCREEN_WIDTH, 3);

        // Score Text
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));

        String scoreText = "🍎 Punkte: " + score;
        g.drawString(scoreText, 20, SCREEN_HEIGHT + 32);

        String highScoreText = "🏆 Highscore: " + highScore;
        FontMetrics fm = g.getFontMetrics();
        int highScoreWidth = fm.stringWidth(highScoreText);
        g.drawString(highScoreText, SCREEN_WIDTH - highScoreWidth - 20, SCREEN_HEIGHT + 32);

        // Steuerung Hinweis
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.setColor(new Color(148, 163, 184));
        String controls = "WASD oder Pfeiltasten zum Steuern";
        int controlsWidth = g.getFontMetrics().stringWidth(controls);
        g.drawString(controls, (SCREEN_WIDTH - controlsWidth) / 2, SCREEN_HEIGHT + 35);
    }

    private void drawGameOver(Graphics2D g) {
        // Overlay
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Game Over Box
        int boxWidth = 300;
        int boxHeight = 180;
        int boxX = (SCREEN_WIDTH - boxWidth) / 2;
        int boxY = (SCREEN_HEIGHT - boxHeight) / 2;

        g.setColor(new Color(30, 30, 40));
        g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        g.setColor(APPLE_COLOR);
        g.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        // Game Over Text
        g.setColor(APPLE_COLOR);
        g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 32));
        String gameOverText = "💀 GAME OVER";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(gameOverText, boxX + (boxWidth - fm.stringWidth(gameOverText)) / 2, boxY + 50);

        // Score
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        String finalScore = "Punkte: " + score;
        fm = g.getFontMetrics();
        g.drawString(finalScore, boxX + (boxWidth - fm.stringWidth(finalScore)) / 2, boxY + 90);

        // Highscore
        if (score >= highScore && score > 0) {
            g.setColor(new Color(255, 215, 0));
            String newRecord = "🎉 Neuer Highscore!";
            g.drawString(newRecord, boxX + (boxWidth - fm.stringWidth(newRecord)) / 2, boxY + 120);
        }

        // Restart Hinweis
        g.setColor(new Color(148, 163, 184));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        String restart = "Drücke LEERTASTE zum Neustarten";
        fm = g.getFontMetrics();
        g.drawString(restart, boxX + (boxWidth - fm.stringWidth(restart)) / 2, boxY + 155);
    }
}
