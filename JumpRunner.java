import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Jump Runner - Ein Mario-ähnliches Parkour-Spiel
 * Springe über Hindernisse und sammle Münzen!
 */
public class JumpRunner extends JFrame {

    public JumpRunner() {
        setTitle("🏃 Jump Runner");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainContainer = new JPanel(new BorderLayout());
        JumpRunnerPanel gamePanel = new JumpRunnerPanel();

        // Home Button Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(5, 5, 10)); // Match SKY_TOP

        JButton homeBtn = new JButton("🏠 Main Menu");
        homeBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        homeBtn.setBackground(new Color(138, 43, 226)); // Purple accent
        homeBtn.setForeground(Color.BLACK);
        homeBtn.setFocusPainted(false);
        homeBtn.setFocusable(false);
        homeBtn.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        homeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        homeBtn.addActionListener(e -> dispose());

        headerPanel.add(homeBtn);

        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(gamePanel, BorderLayout.CENTER);

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
        SwingUtilities.invokeLater(() -> new JumpRunner());
    }
}

class JumpRunnerPanel extends JPanel implements ActionListener {

    // Bildschirm
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int GROUND_Y = HEIGHT - 80;

    // Farben
    private static final Color SKY_TOP = new Color(5, 5, 10);
    private static final Color SKY_BOTTOM = new Color(20, 20, 30);
    private static final Color GROUND_COLOR = new Color(40, 40, 45);
    private static final Color GRASS_COLOR = new Color(30, 30, 35);
    private static final Color PLAYER_COLOR = Color.WHITE; // Strichmännchen
    private static final Color COIN_COLOR = new Color(255, 215, 0);
    private static final Color CLOUD_COLOR = new Color(60, 60, 70, 220);

    // Spieler
    private int playerX = 100;
    private int playerY = GROUND_Y;
    private int playerWidth = 40;
    private int playerHeight = 50;
    private double velocityY = 0;
    private boolean isJumping = false;
    private boolean isDoubleJumpAvailable = true;
    private static final double GRAVITY = 1.5;
    private static final double JUMP_FORCE = -22;

    // Spiel
    private boolean running = true;
    private boolean gameOver = false;
    private int score = 0;
    private int highScore = 0;
    private int distance = 0;
    private double gameSpeed = 9;
    private javax.swing.Timer timer;

    // Hindernisse und Münzen
    private ArrayList<Rectangle> obstacles;
    private ArrayList<Point> coins;
    private ArrayList<int[]> clouds; // x, y, width
    private ArrayList<int[]> mountains; // x, height, width

    // Animation
    private int runFrame = 0;
    private int frameCount = 0;
    private int flashTimer = 0;
    private boolean isFlashing = false;

    public JumpRunnerPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        initGame();
        initGame();
        timer = new javax.swing.Timer(12, this); // ~83 FPS (Smoother)
        timer.start();
    }

    private void initGame() {
        obstacles = new ArrayList<>();
        coins = new ArrayList<>();
        clouds = new ArrayList<>();
        mountains = new ArrayList<>();

        playerY = GROUND_Y;
        velocityY = 0;
        isJumping = false;
        isDoubleJumpAvailable = true;
        running = true;
        gameOver = false;
        score = 0;
        distance = 0;
        distance = 0;
        gameSpeed = 12; // Even faster start

        // Initiale Wolken
        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            clouds.add(new int[] { rand.nextInt(WIDTH), rand.nextInt(100) + 20, rand.nextInt(60) + 40 });
        }

        // Initiale Berge
        for (int i = 0; i < 8; i++) {
            mountains.add(new int[] { i * 150 + rand.nextInt(50), 80 + rand.nextInt(60), 100 + rand.nextInt(80) });
        }

        // Erstes Hindernis nach einer Weile
        spawnObstacle(WIDTH + 200);
    }

    private void handleKeyPress(KeyEvent e) {
        int key = e.getKeyCode();

        if (gameOver) {
            if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_ENTER) {
                initGame();
            }
            return;
        }

        if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            jump();
        }
    }

    private void jump() {
        if (!isJumping) {
            velocityY = JUMP_FORCE;
            isJumping = true;
        } else if (isDoubleJumpAvailable) {
            velocityY = JUMP_FORCE * 0.85;
            isDoubleJumpAvailable = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !gameOver) {
            update();
        }
        repaint();
    }

    private void update() {
        frameCount++;
        distance++;

        // Geschwindigkeit erhöhen
        if (distance % 400 == 0) {
            gameSpeed = Math.min(gameSpeed + 0.5, 35);
        }

        // Spieler Physik
        if (isJumping) {
            velocityY += GRAVITY;
            playerY += (int) velocityY;

            if (playerY >= GROUND_Y) {
                playerY = GROUND_Y;
                velocityY = 0;
                isJumping = false;
                isDoubleJumpAvailable = true;
            }
        }

        // Lauf-Animation
        if (frameCount % 5 == 0) {
            runFrame = (runFrame + 1) % 4;
        }

        // Hindernisse bewegen
        Iterator<Rectangle> obsIterator = obstacles.iterator();
        while (obsIterator.hasNext()) {
            Rectangle obs = obsIterator.next();
            obs.x -= (int) gameSpeed;

            if (obs.x + obs.width < 0) {
                obsIterator.remove();
                score += 10;
            }

            // Kollision
            Rectangle playerRect = new Rectangle(playerX + 5, playerY - playerHeight + 5, playerWidth - 10,
                    playerHeight - 5);
            if (playerRect.intersects(obs)) {
                gameOver = true;
                running = false;
                if (score > highScore) {
                    highScore = score;
                }
            }
        }

        // Neue Hindernisse spawnen
        if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).x < WIDTH - 300) {
            Random rand = new Random();
            if (rand.nextDouble() < 0.02) {
                spawnObstacle(WIDTH);
            }
        }

        // Münzen bewegen
        Iterator<Point> coinIterator = coins.iterator();
        while (coinIterator.hasNext()) {
            Point coin = coinIterator.next();
            coin.x -= (int) gameSpeed;

            if (coin.x < -20) {
                coinIterator.remove();
            }

            // Münze einsammeln
            Rectangle playerRect = new Rectangle(playerX, playerY - playerHeight, playerWidth, playerHeight);
            Rectangle coinRect = new Rectangle(coin.x - 15, coin.y - 15, 30, 30);
            if (playerRect.intersects(coinRect)) {
                coinIterator.remove();
                score += 25;
            }
        }

        // Neue Münzen spawnen
        Random rand = new Random();
        if (rand.nextDouble() < 0.01) {
            int coinY = GROUND_Y - rand.nextInt(100) - 50;
            coins.add(new Point(WIDTH + 20, coinY));
        }

        // Wolken bewegen
        for (int[] cloud : clouds) {
            cloud[0] -= 1;
            if (cloud[0] + cloud[2] < 0) {
                cloud[0] = WIDTH + rand.nextInt(100);
                cloud[1] = rand.nextInt(100) + 20;
            }
        }

        // Blitz Logik
        if (flashTimer > 0) {
            flashTimer--;
            if (flashTimer == 0)
                isFlashing = false;
        } else if (rand.nextInt(120) == 0) { // Häufigere Gewitter
            isFlashing = true;
            flashTimer = 5 + rand.nextInt(10);
        }
    }

    private void spawnObstacle(int x) {
        Random rand = new Random();
        int type = rand.nextInt(6); // Jetzt 6 verschiedene Typen!

        switch (type) {
            case 0: // Kleiner runder Stein
                obstacles.add(new Rectangle(x, GROUND_Y - 25, 25, 25));
                break;
            case 1: // Mittlerer eckiger Stein
                obstacles.add(new Rectangle(x, GROUND_Y - 40, 35, 40));
                break;
            case 2: // Hoher schmaler Stein
                obstacles.add(new Rectangle(x, GROUND_Y - 65, 25, 65));
                break;
            case 3: // Breiter flacher Stein
                obstacles.add(new Rectangle(x, GROUND_Y - 30, 50, 30));
                break;
            case 4: // Doppelstein (zwei kleine nebeneinander)
                obstacles.add(new Rectangle(x, GROUND_Y - 28, 20, 28));
                obstacles.add(new Rectangle(x + 25, GROUND_Y - 28, 20, 28));
                break;
            case 5: // Sehr hoher Stein
                obstacles.add(new Rectangle(x, GROUND_Y - 80, 30, 80));
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2d);
        drawClouds(g2d);
        drawGround(g2d);
        drawObstacles(g2d);
        drawCoins(g2d);
        drawPlayer(g2d);
        drawUI(g2d);

        if (gameOver) {
            drawGameOver(g2d);
        }
    }

    private void drawBackground(Graphics2D g) {
        if (isFlashing) {
            g.setColor(new Color(200, 220, 255));
            g.fillRect(0, 0, WIDTH, HEIGHT);

            // Blitzeffekt Linien
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(3));
            int startX = new Random().nextInt(WIDTH);
            int currentX = startX;
            int currentY = 0;
            while (currentY < HEIGHT / 2) {
                int nextX = currentX + (new Random().nextInt(40) - 20);
                int nextY = currentY + new Random().nextInt(30);
                g.drawLine(currentX, currentY, nextX, nextY);
                currentX = nextX;
                currentY = nextY;
            }
        } else {
            GradientPaint sky = new GradientPaint(0, 0, SKY_TOP, 0, HEIGHT, SKY_BOTTOM);
            g.setPaint(sky);
            g.fillRect(0, 0, WIDTH, HEIGHT);
        }

        // Berge im Hintergrund zeichnen
        drawMountains(g);
    }

    private void drawMountains(Graphics2D g) {
        for (int[] mountain : mountains) {
            int x = mountain[0];
            int h = mountain[1];
            int w = mountain[2];

            // Hintere Bergschicht (dunkler)
            g.setColor(new Color(25, 25, 35));
            int[] backX = { x - 20, x + w / 2 - 10, x + w + 20 };
            int[] backY = { GROUND_Y, (int) (GROUND_Y - h * 0.7), GROUND_Y };
            g.fillPolygon(backX, backY, 3);

            // Hauptberg
            g.setColor(new Color(40, 40, 50));
            int[] xPoints = { x, x + w / 2, x + w };
            int[] yPoints = { GROUND_Y, GROUND_Y - h, GROUND_Y };
            g.fillPolygon(xPoints, yPoints, 3);

            // Schatten auf der rechten Seite
            g.setColor(new Color(30, 30, 40));
            int[] shadowX = { x + w / 2, x + w, x + w / 2 + w / 4 };
            int[] shadowY = { GROUND_Y - h, GROUND_Y, GROUND_Y - h / 2 };
            g.fillPolygon(shadowX, shadowY, 3);

            // Felsstrukturen (Kanten)
            g.setColor(new Color(50, 50, 60));
            g.setStroke(new BasicStroke(2));
            g.drawLine(x + w / 4, GROUND_Y - h / 3, x + w / 2 - 10, GROUND_Y - h + 15);
            g.drawLine(x + w / 2 + w / 6, GROUND_Y - h / 2, x + w - w / 4, GROUND_Y - h / 4);

            // Schnee auf der Spitze (mehrschichtig und schöner)
            // Basis-Schnee
            g.setColor(new Color(200, 210, 220));
            int[] snowX1 = { x + w / 2 - 25, x + w / 2, x + w / 2 + 25 };
            int[] snowY1 = { GROUND_Y - h + 30, GROUND_Y - h, GROUND_Y - h + 30 };
            g.fillPolygon(snowX1, snowY1, 3);

            // Mittlere Schneeschicht
            g.setColor(new Color(220, 225, 235));
            int[] snowX2 = { x + w / 2 - 18, x + w / 2, x + w / 2 + 18 };
            int[] snowY2 = { GROUND_Y - h + 20, GROUND_Y - h, GROUND_Y - h + 20 };
            g.fillPolygon(snowX2, snowY2, 3);

            // Helle Spitze
            g.setColor(new Color(240, 245, 250));
            int[] snowX3 = { x + w / 2 - 10, x + w / 2, x + w / 2 + 10 };
            int[] snowY3 = { GROUND_Y - h + 12, GROUND_Y - h, GROUND_Y - h + 12 };
            g.fillPolygon(snowX3, snowY3, 3);

            // Glitzer-Effekt (kleine weiße Punkte)
            g.setColor(Color.WHITE);
            Random sparkleRand = new Random(x + h); // Fixer Seed für konsistente Glitzer
            for (int i = 0; i < 5; i++) {
                int sx = x + w / 2 - 15 + sparkleRand.nextInt(30);
                int sy = GROUND_Y - h + 5 + sparkleRand.nextInt(20);
                g.fillRect(sx, sy, 2, 2);
            }

            // Berge bewegen (langsam)
            mountain[0] -= 1;
            if (mountain[0] + w < 0) {
                mountain[0] = WIDTH + new Random().nextInt(100);
                mountain[1] = 80 + new Random().nextInt(60);
                mountain[2] = 100 + new Random().nextInt(80);
            }
        }
    }

    private void drawClouds(Graphics2D g) {
        g.setColor(CLOUD_COLOR);
        for (int[] cloud : clouds) {
            g.fillOval(cloud[0], cloud[1], cloud[2], cloud[2] / 2);
            g.fillOval(cloud[0] + cloud[2] / 3, cloud[1] - 10, cloud[2] / 2, cloud[2] / 3);
            g.fillOval(cloud[0] - cloud[2] / 4, cloud[1] + 5, cloud[2] / 2, cloud[2] / 3);
        }
    }

    private void drawGround(Graphics2D g) {
        // Gras
        g.setColor(GRASS_COLOR);
        g.fillRect(0, GROUND_Y, WIDTH, 15);

        // Erde
        g.setColor(GROUND_COLOR);
        g.fillRect(0, GROUND_Y + 15, WIDTH, HEIGHT - GROUND_Y - 15);

        // Gras-Details (Dunkles Grau statt Grün)
        g.setColor(new Color(45, 45, 50));
        for (int i = 0; i < WIDTH; i += 20) {
            int offset = (int) ((distance + i) % 20);
            g.fillPolygon(
                    new int[] { i - offset, i + 5 - offset, i + 10 - offset },
                    new int[] { GROUND_Y, GROUND_Y - 8, GROUND_Y },
                    3);
        }
    }

    private void drawObstacles(Graphics2D g) {
        for (Rectangle obs : obstacles) {
            // Schatten (dezent)
            g.setColor(new Color(0, 0, 0, 80));
            g.fillRect(obs.x + 2, obs.y + obs.height - 2, obs.width, 5);

            // Verschiedene Steinarten mit unterschiedlichen Texturen
            // Basis-Stein
            g.setColor(new Color(80, 80, 90));
            g.fillRoundRect(obs.x, obs.y, obs.width, obs.height, 8, 8);

            // Highlight für 3D-Effekt
            g.setColor(new Color(120, 120, 130));
            g.fillRoundRect(obs.x + 2, obs.y + 2, obs.width / 2, obs.height / 3, 5, 5);

            // Dunkle Seite
            g.setColor(new Color(50, 50, 60));
            g.fillRoundRect(obs.x + obs.width - obs.width / 3, obs.y + obs.height / 2, obs.width / 3, obs.height / 2,
                    5, 5);

            // Risse im Stein (Details)
            g.setColor(new Color(40, 40, 50));
            g.setStroke(new BasicStroke(1));
            if (obs.height > 40) {
                g.drawLine(obs.x + obs.width / 3, obs.y + 10, obs.x + obs.width / 2, obs.y + obs.height / 2);
                g.drawLine(obs.x + obs.width * 2 / 3, obs.y + obs.height / 3, obs.x + obs.width - 5,
                        obs.y + obs.height - 10);
            }
        }
    }

    private void drawCoins(Graphics2D g) {
        for (Point coin : coins) {
            // Glanz
            g.setColor(new Color(255, 255, 200));
            g.fillOval(coin.x - 12, coin.y - 12, 24, 24);

            // Münze
            g.setColor(COIN_COLOR);
            g.fillOval(coin.x - 10, coin.y - 10, 20, 20);

            // Highlight
            g.setColor(new Color(255, 255, 150));
            g.fillOval(coin.x - 6, coin.y - 6, 8, 8);

            // Symbol
            g.setColor(new Color(200, 150, 0));
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("$", coin.x - 4, coin.y + 4);
        }
    }

    private void drawPlayer(Graphics2D g) {
        int x = playerX;
        int y = playerY - playerHeight;

        // Strichmännchen Design
        g.setColor(PLAYER_COLOR);
        g.setStroke(new BasicStroke(3));

        // Kopf (Kreis)
        g.drawOval(x + 15, y + 5, 15, 15);

        // Körper (vertikale Linie)
        g.drawLine(x + 22, y + 20, x + 22, y + 35);

        // Arme (Animation beim Laufen)
        int armSwing = 0;
        if (!isJumping) {
            armSwing = (frameCount % 10 < 5) ? -3 : 3;
        }

        // Linker Arm
        g.drawLine(x + 22, y + 23, x + 12, y + 28 + armSwing);
        // Rechter Arm
        g.drawLine(x + 22, y + 23, x + 32, y + 28 - armSwing);

        // Beine (Animation beim Laufen)
        int legSwing = 0;
        if (!isJumping) {
            legSwing = (frameCount % 10 < 5) ? -4 : 4;
        } else {
            // Beim Springen: Beine angewinkelt
            g.drawLine(x + 22, y + 35, x + 18, y + 42);
            g.drawLine(x + 22, y + 35, x + 26, y + 42);
            return;
        }

        // Linkes Bein
        g.drawLine(x + 22, y + 35, x + 15, y + 50 + legSwing);
        // Rechtes Bein
        g.drawLine(x + 22, y + 35, x + 29, y + 50 - legSwing);
    }

    private void drawUI(Graphics2D g) {
        // Score Box
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(10, 10, 150, 60, 10, 10);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g.drawString("🏆 Score: " + score, 20, 35);
        g.drawString("⚡ Speed: " + String.format("%.1f", gameSpeed), 20, 58);

        // Highscore
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(WIDTH - 160, 10, 150, 35, 10, 10);
        g.setColor(COIN_COLOR);
        g.drawString("👑 Best: " + highScore, WIDTH - 145, 33);

        // Controls Hinweis
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRoundRect(WIDTH / 2 - 100, HEIGHT - 30, 200, 25, 10, 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.drawString("SPACE / ↑ zum Springen (2x möglich!)", WIDTH / 2 - 90, HEIGHT - 12);
    }

    private void drawGameOver(Graphics2D g) {
        // Overlay
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Box
        int boxW = 320;
        int boxH = 200;
        int boxX = (WIDTH - boxW) / 2;
        int boxY = (HEIGHT - boxH) / 2;

        g.setColor(new Color(40, 40, 50));
        g.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);
        g.setColor(PLAYER_COLOR);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(boxX, boxY, boxW, boxH, 20, 20);

        // Text
        g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 32));
        g.setColor(PLAYER_COLOR);
        String title = "💥 GAME OVER";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, boxX + (boxW - fm.stringWidth(title)) / 2, boxY + 50);

        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        String scoreText = "Score: " + score;
        fm = g.getFontMetrics();
        g.drawString(scoreText, boxX + (boxW - fm.stringWidth(scoreText)) / 2, boxY + 90);

        if (score >= highScore && score > 0) {
            g.setColor(COIN_COLOR);
            String newRecord = "🎉 Neuer Highscore!";
            g.drawString(newRecord, boxX + (boxW - fm.stringWidth(newRecord)) / 2, boxY + 120);
        }

        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g.setColor(new Color(180, 180, 180));
        String restart = "Drücke SPACE zum Neustarten";
        fm = g.getFontMetrics();
        g.drawString(restart, boxX + (boxW - fm.stringWidth(restart)) / 2, boxY + 170);
    }
}
