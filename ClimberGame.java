import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.List;

/**
 * Climber Game - Ein vertikales Plattform-Spiel
 * Springe so hoch wie möglich!
 */
public class ClimberGame extends JFrame {

    public ClimberGame() {
        setTitle("🧗 Climber Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainContainer = new JPanel(new BorderLayout());
        ClimberGamePanel gamePanel = new ClimberGamePanel();

        // Home Button Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(20, 20, 40));

        JButton homeBtn = new JButton("🏠 Arcade");
        homeBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        homeBtn.setBackground(new Color(255, 140, 0)); // Orange accent
        homeBtn.setForeground(Color.WHITE);
        homeBtn.setFocusPainted(false);
        homeBtn.setFocusable(false);
        homeBtn.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        homeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        homeBtn.addActionListener(e -> dispose());

        JButton shopBtn = new JButton("🛒 Shop");
        shopBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        shopBtn.setBackground(new Color(255, 215, 0));
        shopBtn.setForeground(Color.BLACK);
        shopBtn.setFocusPainted(false);
        shopBtn.setFocusable(false);
        shopBtn.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        shopBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        shopBtn.addActionListener(e -> gamePanel.openShop(this));

        headerPanel.add(homeBtn);
        headerPanel.add(shopBtn);

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
        SwingUtilities.invokeLater(() -> new ClimberGame());
    }
}

class ClimberGamePanel extends JPanel implements ActionListener {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 800;

    private static final Color PLATFORM_COLOR = new Color(100, 200, 100);
    private static final Color TEXT_COLOR = new Color(50, 50, 50); // Dunklerer Text für Kontrast
    private static final Color GROUND_COLOR = new Color(34, 139, 34); // Forest Green
    private static final Color TREE_TRUNK = new Color(101, 67, 33);
    private static final Color TREE_LEAVES = new Color(0, 100, 0);

    // Meteore
    private ArrayList<Meteor> meteors;
    private ArrayList<Point> stars;
    private double groundY; // Position des Bodens (für Bäume/Hintergrund)

    // Spieler
    private int playerX = WIDTH / 2;
    private int playerY = HEIGHT - 150;
    private int playerWidth = 30;
    private int playerHeight = 30;
    private double velocityX = 0;
    private double velocityY = 0;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    // Physik
    // Physik
    private static final double GRAVITY = 0.9; // Very strong gravity
    // JUMP_FORCE and MOVE_SPEED now in Avatar

    // --- AVATAR SYSTEM ---
    static class Avatar {
        String name;
        Color color;
        double jumpForce;
        double moveSpeed;
        boolean hasDash;
        int price;
        boolean owned;

        public Avatar(String name, Color color, double jumpForce, double moveSpeed, boolean hasDash, int price,
                boolean owned) {
            this.name = name;
            this.color = color;
            this.jumpForce = jumpForce;
            this.moveSpeed = moveSpeed;
            this.hasDash = hasDash;
            this.price = price;
            this.owned = owned;
        }
    }

    private List<Avatar> avatars = new ArrayList<>();
    private Avatar currentAvatar;
    private int coins = 0;
    private int nextCoinThreshold = 1000;

    // Dash Variables
    private boolean isDashing = false;
    private int dashTimer = 0;
    private int dashCooldown = 0;
    private static final int DASH_DURATION = 15;
    private static final int DASH_COOLDOWN_TIME = 100;
    private static final double DASH_SPEED = 30;
    private static final double DASH_UPWARD_FORCE = -25;

    // Spiel Welt
    private ArrayList<Rectangle> platforms;
    private int score = 0;
    private int highScore = 0;
    private boolean gameOver = false;
    private javax.swing.Timer timer;
    private Random rand = new Random();

    public ClimberGamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);

        // Init Avatars
        // Init Avatars (Even Faster speeds & STRONGER Jumps for high gravity)
        avatars.add(new Avatar("Standard", Color.WHITE, -22, 12, false, 0, true));
        avatars.add(new Avatar("Jumper (High Jump)", Color.YELLOW, -26, 12, false, 50, false));
        avatars.add(new Avatar("Dasher (Speed)", Color.CYAN, -22, 15, true, 200, false));
        avatars.add(new Avatar("Ultimate", Color.MAGENTA, -28, 18, true, 500, false));
        currentAvatar = avatars.get(0);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
                    leftPressed = true;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
                    rightPressed = true;
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP
                        || e.getKeyCode() == KeyEvent.VK_W) {
                    // Manuelles Springen, wenn man auf dem Boden oder einer Plattform steht
                    // Dies ist eine Design-Entscheidung: Soll er automatisch springen (Doodle Jump)
                    // oder manuell?
                    // User Request: "you have to jump from plattform zu plattform" -> impliziert
                    // aktives Handeln
                    if (velocityY == 0) { // Einfache Bodenprüfung
                        velocityY = currentAvatar.jumpForce;
                    }
                }

                // Dash Input
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    if (currentAvatar.hasDash && dashCooldown <= 0 && !isDashing) {
                        isDashing = true;
                        dashTimer = DASH_DURATION;
                        dashCooldown = DASH_COOLDOWN_TIME;
                        // Dash direction - jetzt nach oben!
                        velocityY = DASH_UPWARD_FORCE; // Starker Schub nach oben
                        if (leftPressed)
                            velocityX = -DASH_SPEED;
                        else if (rightPressed)
                            velocityX = DASH_SPEED;
                        else
                            velocityX = 0; // Kein horizontaler Schub wenn keine Richtung gedrückt
                    }
                }

                if (gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
                    resetGame();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
                    leftPressed = false;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
                    rightPressed = false;
            }
        });

        resetGame();
        resetGame();
        timer = new javax.swing.Timer(12, this); // ~83 FPS (Smoother)
        timer.start();
    }

    private void resetGame() {
        playerX = WIDTH / 2;
        playerY = HEIGHT - 150;
        velocityX = 0;
        velocityY = 0;
        platforms = new ArrayList<>();
        meteors = new ArrayList<>();
        score = 0;
        gameOver = false;
        groundY = HEIGHT; // Boden startet ganz unten

        nextCoinThreshold = 1000;
        isDashing = false;
        dashCooldown = 0;

        // Start Plattform
        platforms.add(new Rectangle(WIDTH / 2 - 50, HEIGHT - 50, 100, 20));

        // Generiere erste Plattformen
        generatePlatforms(HEIGHT - 100);
    }

    private void generatePlatforms(int maxY) {
        int currentY = maxY;
        while (currentY > -100) {
            currentY -= (60 + rand.nextInt(60)); // Abstand zwischen Plattformen
            int w = 60 + rand.nextInt(60);
            int x = rand.nextInt(WIDTH - w);
            platforms.add(new Rectangle(x, currentY, w, 15));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            update();
        }
        repaint();
    }

    private void update() {
        // Meteor Spawning
        // Meteor Spawning - Weniger & Langsamer (User Request)
        if (rand.nextInt(100) < 1) { // 1% Chance (weniger als vorher 3%)
            int sz = 30 + rand.nextInt(40);
            // Langsamer: 3 bis 7 Speed (vorher 6-15)
            meteors.add(new Meteor(rand.nextInt(WIDTH), -80, sz, 3 + rand.nextInt(5)));
        }

        // Meteor Bewegung & Kollision
        Iterator<Meteor> mit = meteors.iterator();
        while (mit.hasNext()) {
            Meteor m = mit.next();
            m.y += m.speed;

            // Kollisionsprüfung mit Spieler
            // Einfache Rechteck-Kollision
            if (new Rectangle(m.x, m.y, m.size, m.size).intersects(
                    new Rectangle(playerX, playerY, playerWidth, playerHeight))) {
                gameOver = true;
            }

            if (m.y > HEIGHT) {
                mit.remove();
            }
        }

        // Bewegung Links/Rechts
        if (isDashing) {
            dashTimer--;
            if (dashTimer <= 0)
                isDashing = false;
            // Keep dash velocity (set in keyPressed)
        } else {
            if (dashCooldown > 0)
                dashCooldown--;

            if (leftPressed)
                velocityX = -currentAvatar.moveSpeed;
            else if (rightPressed)
                velocityX = currentAvatar.moveSpeed;
            else
                velocityX = 0;
        }

        playerX += velocityX;

        // Screen Wrap (wie Pacman)
        if (playerX + playerWidth < 0)
            playerX = WIDTH;
        if (playerX > WIDTH)
            playerX = -playerWidth;

        // Schwerkraft
        velocityY += GRAVITY;
        playerY += velocityY;

        // Kollision mit Plattformen
        // Nur wenn wir fallen (velocityY > 0)
        if (velocityY > 0) {
            for (Rectangle p : platforms) {
                // Prüfen ob Spielerfuß die Plattform berührt
                if (playerY + playerHeight >= p.y && playerY + playerHeight <= p.y + p.height + 10 &&
                        playerX + playerWidth > p.x && playerX < p.x + p.width) {

                    // Wir landen drauf -> reset physics
                    playerY = p.y - playerHeight;
                    velocityY = 0;
                }
            }
        }

        // Scrolling (Kamera bewegt sich nach oben wenn Spieler hoch springt)
        if (playerY < HEIGHT / 2) {
            int offset = HEIGHT / 2 - playerY;
            playerY = HEIGHT / 2; // Spieler bleibt mittig

            // Alles nach unten schieben
            for (Rectangle p : platforms) {
                p.y += offset;
            }

            // Meteore auch verschieben (scheinbar schneller wenn Kamera hoch geht)
            for (Meteor m : meteors) {
                m.y += offset;
            }

            // Boden/Bäume verschieben
            groundY += offset;

            // Score erhöhen
            score += offset;

            // Coins logic
            if (score >= nextCoinThreshold) {
                coins += 10;
                nextCoinThreshold += 1000;
            }

            if (score > highScore)
                highScore = score;

            // Alte Plattformen entfernen und neue generieren
            removeOldPlatforms();
            addNewPlatforms();
        }

        // Game Over Bedingung (Fallen)
        if (playerY > HEIGHT) {
            gameOver = true;
        }
    }

    private void removeOldPlatforms() {
        Iterator<Rectangle> it = platforms.iterator();
        while (it.hasNext()) {
            Rectangle p = it.next();
            if (p.y > HEIGHT) {
                it.remove();
            }
        }
    }

    private void addNewPlatforms() {
        // Find highest platform
        int highestY = HEIGHT;
        for (Rectangle p : platforms) {
            if (p.y < highestY)
                highestY = p.y;
        }

        if (highestY > 100) {
            generatePlatforms(highestY);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- HINTERGRUND & ATMOSPHÄRE ---
        // Dynamischer Himmel: Je höher man kommt, desto dunkler (Weltraum)
        // Score/10000 bestimmt den Fortschritt (0.0 bis 1.0)
        float progress = Math.min(1.0f, Math.max(0, score / 15000.0f));

        Color skyTopDay = new Color(135, 206, 235);
        Color skyBotDay = new Color(200, 230, 255);
        Color skyTopSpace = new Color(5, 5, 20); // Fast Schwarz
        Color skyBotSpace = new Color(25, 25, 60); // Dunkelblau

        Color currentTop = interpolateColor(skyTopDay, skyTopSpace, progress);
        Color currentBot = interpolateColor(skyBotDay, skyBotSpace, progress);

        GradientPaint gp = new GradientPaint(0, 0, currentTop, 0, HEIGHT, currentBot);
        g2.setPaint(gp);
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        // Sterne (nur sichtbar wenn es dunkler wird)
        if (progress > 0.1f) {
            drawStars(g2, progress);
        }

        // Planeten (Im Weltraum - weit oben)
        if (progress > 0.2f) {
            drawPlanets(g2, progress);
        }

        // Sonne (sinkt nach unten)
        int sunY = 50 + (int) (progress * HEIGHT * 1.5);
        if (sunY < HEIGHT + 150) {
            // Glow
            g2.setColor(new Color(255, 255, 0, 40));
            g2.fillOval(WIDTH - 160, sunY - 20, 120, 120);
            g2.setColor(new Color(255, 200, 0, 80));
            g2.fillOval(WIDTH - 150, sunY - 10, 100, 100);
            // Kern
            g2.setColor(Color.YELLOW);
            g2.fillOval(WIDTH - 140, sunY, 80, 80);
        }

        // Mond (kommt von oben wenn Sonne weg ist)
        if (progress > 0.4f) {
            int moonY = -100 + (int) ((progress - 0.4f) * HEIGHT);
            if (moonY > -100) {
                g2.setColor(new Color(240, 240, 240));
                g2.fillOval(80, moonY, 60, 60);
                g2.setColor(new Color(200, 200, 210)); // Krater
                g2.fillOval(100, moonY + 25, 12, 8);
                g2.fillOval(115, moonY + 15, 8, 8);
            }
        }

        // --- LANDSCHAFT (Bäume, Berge) ---
        // Nur sichtbar in Bodennähe
        if (groundY < HEIGHT + 400) {
            int baseY = (int) groundY;

            // Berge im Hintergrund
            g2.setColor(new Color(60, 80, 70));
            int[] mx = { 0, 150, 300, 500, WIDTH, WIDTH, 0 };
            int[] my = { baseY, baseY - 200, baseY - 120, baseY - 250, baseY - 80, baseY, baseY };
            g2.fillPolygon(mx, my, 7);

            // Stadt im Hintergrund
            drawCity(g2, baseY - 20);

            // Grasboden
            g2.setColor(GROUND_COLOR);
            g2.fillRect(0, baseY - 20, WIDTH, 600);

            // Detaillierte Bäume
            for (int i = 30; i < WIDTH; i += 120) {
                drawTree(g2, i, baseY - 20);
            }
        }

        // --- SPIELOBJEKTE ---

        // Plattformen
        for (Rectangle p : platforms) {
            if (progress > 0.6f) {
                // Sci-Fi Plattformen im Weltraum
                g2.setColor(new Color(50, 50, 60));
                g2.fillRoundRect(p.x, p.y, p.width, p.height, 5, 5);
                g2.setColor(Color.CYAN); // Neon Kante
                g2.drawRoundRect(p.x, p.y, p.width, p.height, 5, 5);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2.fillRect(p.x + 2, p.y + 2, p.width - 4, p.height - 4);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            } else {
                // Normale Plattformen
                g2.setColor(PLATFORM_COLOR);
                g2.fillRoundRect(p.x, p.y, p.width, p.height, 10, 10);
                g2.setColor(new Color(150, 230, 150));
                g2.fillRect(p.x + 5, p.y + 2, p.width - 10, 4); // Glanz
            }
        }

        // Meteoriten
        for (Meteor m : meteors) {
            drawMeteor(g2, m);
        }

        // Spieler
        drawPlayer(g2);

        // UI / Score
        g2.setColor(progress > 0.5 ? Color.WHITE : TEXT_COLOR);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.drawString("Höhe: " + (score / 10) + "m", 20, 30);
        g2.drawString("Highscore: " + (highScore / 10) + "m", 20, 55);

        g2.setColor(new Color(255, 215, 0));
        g2.drawString("Münzen: " + coins, 20, 80);

        if (currentAvatar != null && currentAvatar.hasDash) {
            g2.setColor(dashCooldown > 0 ? Color.GRAY : Color.CYAN);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2.drawString("Dash: " + (dashCooldown > 0 ? "Wait" : "READY (Shift)"), 20, 105);
        }

        if (gameOver) {
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, WIDTH, HEIGHT);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 40));
            String msg = "GAME OVER";
            int msgW = g2.getFontMetrics().stringWidth(msg);
            g2.drawString(msg, (WIDTH - msgW) / 2, HEIGHT / 2 - 20);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            String sub = "Drücke Space zum Neustart";
            int subW = g2.getFontMetrics().stringWidth(sub);
            g2.drawString(sub, (WIDTH - subW) / 2, HEIGHT / 2 + 30);
        }
    }

    // --- HELPER METHODS ---

    private void drawTree(Graphics2D g2, int x, int y) {
        // Stamm
        g2.setColor(TREE_TRUNK);
        g2.fillRect(x + 10, y - 40, 14, 40);

        // Baumkrone (mehrere Schichten)
        g2.setColor(TREE_LEAVES); // Dunkelgrün
        g2.fillPolygon(new int[] { x - 10, x + 17, x + 44 }, new int[] { y - 30, y - 80, y - 30 }, 3);
        g2.setColor(new Color(34, 160, 34)); // Hellgrün
        g2.fillPolygon(new int[] { x - 5, x + 17, x + 39 }, new int[] { y - 50, y - 95, y - 50 }, 3);
        g2.setColor(new Color(50, 205, 50)); // Spitze
        g2.fillPolygon(new int[] { x, x + 17, x + 34 }, new int[] { y - 70, y - 110, y - 70 }, 3);
    }

    private void drawMeteor(Graphics2D g2, Meteor m) {
        // Basis-Stein
        g2.setColor(new Color(119, 136, 153)); // LightSlateGray
        g2.fillOval(m.x, m.y, m.size, m.size);

        // Schattierung unten rechts (für 3D-Look)
        g2.setColor(new Color(47, 79, 79)); // DarkSlateGray
        g2.fillArc(m.x, m.y, m.size, m.size, 225, 120);

        // Krater (Löcher im Stein)
        g2.setColor(new Color(60, 60, 70));

        // Großer Krater
        g2.fillOval(m.x + m.size / 3, m.y + m.size / 4, m.size / 3, m.size / 3);

        // Kleinere Krater
        g2.fillOval(m.x + m.size / 6, m.y + m.size / 2, m.size / 5, m.size / 5);
        g2.fillOval(m.x + m.size / 2 + m.size / 6, m.y + m.size / 3 * 2, m.size / 6, m.size / 6);

        // Highlight im großen Krater (Tiefe)
        g2.setColor(new Color(40, 40, 50));
        g2.fillArc(m.x + m.size / 3, m.y + m.size / 4, m.size / 3, m.size / 3, 45, 180);
    }

    private void drawPlayer(Graphics2D g2) {
        // Körper
        g2.setColor(currentAvatar != null ? currentAvatar.color : Color.WHITE);
        if (isDashing)
            g2.setColor(Color.ORANGE);
        g2.fillRoundRect(playerX, playerY, playerWidth, playerHeight, 10, 10);
        // Stirnband / Detail
        g2.setColor(Color.RED);
        g2.fillRect(playerX, playerY + 5, playerWidth, 4);

        // Gesicht
        g2.setColor(Color.BLACK);
        int eyeX = (velocityX < -0.1) ? 6 : (velocityX > 0.1 ? 14 : 10);
        g2.fillOval(playerX + eyeX, playerY + 12, 4, 4);
        g2.fillOval(playerX + eyeX + 10, playerY + 12, 4, 4);
    }

    private void drawStars(Graphics2D g2, float opacity) {
        if (stars == null) {
            stars = new ArrayList<>();
            for (int i = 0; i < 150; i++) {
                stars.add(new Point(rand.nextInt(WIDTH), rand.nextInt(HEIGHT)));
            }
        }
        g2.setColor(new Color(255, 255, 255, (int) (255 * opacity)));
        for (Point p : stars) {
            int s = (p.x % 3 == 0) ? 2 : 1; // Unterschiedliche Größe
            g2.fillRect(p.x, p.y, s, s);
        }
    }

    private Color interpolateColor(Color c1, Color c2, float f) {
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * f);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * f);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * f);
        return new Color(Math.max(0, Math.min(255, r)),
                Math.max(0, Math.min(255, g)),
                Math.max(0, Math.min(255, b)));
    }

    private void drawCity(Graphics2D g2, int baseY) {
        // Fixer Seed, damit die Häuser nicht flackern
        Random cityRand = new Random(12345);

        for (int x = 0; x < WIDTH; x += 30 + cityRand.nextInt(50)) {
            int h = 100 + cityRand.nextInt(150);
            int w = 40 + cityRand.nextInt(30);

            // Gebäude
            g2.setColor(new Color(60, 60, 70));
            g2.fillRect(x, baseY - h, w, h);

            // Fenster - leuchten manchmal
            g2.setColor(new Color(255, 255, 100, 180));
            for (int wx = x + 5; wx < x + w - 5; wx += 10) {
                for (int wy = baseY - h + 10; wy < baseY - 10; wy += 15) {
                    if (cityRand.nextBoolean()) {
                        g2.fillRect(wx, wy, 6, 8);
                    }
                }
            }
        }
    }

    private void drawPlanets(Graphics2D g2, float progress) {
        Random pRand = new Random(54321); // Fixer Seed

        // Ein paar Planeten
        for (int i = 0; i < 4; i++) {
            int sz = 50 + pRand.nextInt(80);
            int px = pRand.nextInt(WIDTH);
            int py = 50 + pRand.nextInt(300); // Oberer Bereich

            // Sichtbarkeit dimmen
            float alpha = (progress - 0.2f) * 1.5f;
            if (alpha > 1f)
                alpha = 1f;
            if (alpha < 0f)
                alpha = 0f;

            if (alpha > 0) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

                Color pColor = new Color(pRand.nextInt(255), pRand.nextInt(255), pRand.nextInt(255));
                g2.setColor(pColor);
                g2.fillOval(px, py, sz, sz);

                // Schattierung / Detail
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillArc(px, py, sz, sz, 90, 180);

                // Ring (Chance 50%)
                if (pRand.nextBoolean()) {
                    g2.setColor(new Color(200, 200, 255, 150));
                    g2.setStroke(new BasicStroke(4));
                    g2.drawOval(px - 10, py + sz / 2 - 10, sz + 20, 20);
                    g2.setStroke(new BasicStroke(1));
                }

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
        }
    }

    public void openShop(JFrame owner) {
        timer.stop();
        JDialog dialog = new JDialog(owner, "Avatar Shop", true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(owner);
        dialog.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout());
        JLabel coinLbl = new JLabel("Deine Münzen: " + coins);
        coinLbl.setFont(new Font("Arial", Font.BOLD, 20));
        header.add(coinLbl);
        dialog.add(header, BorderLayout.NORTH);

        // List
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        for (Avatar a : avatars) {
            JPanel p = new JPanel(new BorderLayout(10, 10));
            p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            p.setMaximumSize(new Dimension(450, 80));

            // Icon
            JLabel icon = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(a.color);
                    g.fillOval(5, 5, 40, 40);
                    g.setColor(Color.BLACK); // Simple eye
                    g.fillOval(15, 15, 5, 5);
                    g.fillOval(30, 15, 5, 5);
                }
            };
            icon.setPreferredSize(new Dimension(50, 50));
            p.add(icon, BorderLayout.WEST);

            // Stats
            String stats = "<html><b>" + a.name + "</b><br>Jump: " + Math.abs(a.jumpForce) + " | Speed: " + a.moveSpeed
                    + (a.hasDash ? " | ⚡ Dash" : "") + "</html>";
            JLabel info = new JLabel(stats);
            p.add(info, BorderLayout.CENTER);

            // Button
            JButton btn = new JButton();
            if (a.owned) {
                if (currentAvatar == a) {
                    btn.setText("Aktiv");
                    btn.setEnabled(false);
                } else {
                    btn.setText("Ausrüsten");
                    btn.addActionListener(ev -> {
                        currentAvatar = a;
                        dialog.dispose();
                        repaint();
                    });
                }
            } else {
                btn.setText("Kaufen (" + a.price + ")");
                if (coins >= a.price) {
                    btn.addActionListener(ev -> {
                        coins -= a.price;
                        a.owned = true;
                        currentAvatar = a;
                        dialog.dispose();
                        repaint();
                    });
                } else {
                    btn.setEnabled(false);
                }
            }
            p.add(btn, BorderLayout.EAST);
            list.add(p);

            // Separator
            list.add(new JSeparator());
        }

        dialog.add(new JScrollPane(list), BorderLayout.CENTER);

        JButton close = new JButton("Schließen");
        close.addActionListener(ev -> dialog.dispose());
        dialog.add(close, BorderLayout.SOUTH);

        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                if (!gameOver)
                    timer.start();
            }
        });

        dialog.setVisible(true);
    }

    // Interne Klasse für Meteore
    class Meteor {
        int x, y, size, speed;

        public Meteor(int x, int y, int size, int speed) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speed = speed;
        }
    }
}
