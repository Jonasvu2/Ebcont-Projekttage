import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * Arcade - Spieleübersicht
 * Wähle ein Spiel aus und starte es!
 */
public class Arcade extends JFrame {

    // Farben für das Design
    private static final Color BACKGROUND_COLOR = new Color(15, 23, 42);
    private static final Color CARD_COLOR = new Color(30, 41, 59, 150);
    private static final Color CARD_HOVER_COLOR = new Color(51, 65, 85, 210);
    private static final Color ACCENT_COLOR = new Color(99, 102, 241);
    private static final Color TEXT_COLOR = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);

    // Spiele-Daten: {Name, Klasse, Farbe}
    private static final Object[][] GAMES = {
            { "Jump Runner", "JumpRunner", new Color(138, 43, 226) },
            { "Climber Game", "ClimberGame", new Color(255, 140, 0) },
            { "Tier Memory", "AnimalMemoryGame", new Color(99, 102, 241) },
            { "Snake", "SnakeGame", new Color(18, 185, 129) },
            { "Galactic Tycoon", "TycoonGame", new Color(129, 140, 248) },
            { "Schere Stein Papier", "SchereSteinPapierGUI", new Color(239, 68, 68) }
    };

    public Arcade() {
        setTitle("🎮 Arcade - Spielesammlung");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initializeUI();

        pack();
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeUI() {
        BackgroundPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Spiele Grid
        JPanel gamesPanel = createGamesPanel();
        mainPanel.add(gamesPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Titel mit Glow
        GlowLabel titleLabel = new GlowLabel("ARCADE");
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 48));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Untertitel
        JLabel subtitleLabel = new JLabel("Wähle ein Spiel und hab Spaß!");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Trennlinie
        JPanel divider = new JPanel();
        divider.setMaximumSize(new Dimension(200, 3));
        divider.setBackground(ACCENT_COLOR);
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(15));
        headerPanel.add(divider);

        return headerPanel;
    }

    private JPanel createGamesPanel() {
        JPanel gamesPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        gamesPanel.setBackground(BACKGROUND_COLOR);

        for (Object[] game : GAMES) {
            JPanel gameCard = createGameCard(
                    (String) game[0],
                    (String) game[1],
                    (Color) game[2]);
            gamesPanel.add(gameCard);
        }

        return gamesPanel;
    }

    private JPanel createGameCard(String name, String className, Color accentColor) {
        JPanel card = new TransparentPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(CARD_COLOR);
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 80), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Titel Container
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(nameLabel);

        // Play Button mit schönerem Design
        JButton playButton = new JButton("Spielen") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        playButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        playButton.setBackground(accentColor);
        playButton.setForeground(Color.WHITE);
        playButton.setFocusPainted(false);
        playButton.setBorderPainted(false);
        playButton.setContentAreaFilled(false);
        playButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        playButton.setPreferredSize(new Dimension(80, 28));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(playButton);

        // Button Hover
        playButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                playButton.setBackground(accentColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                playButton.setBackground(accentColor);
            }
        });

        playButton.addActionListener(e -> launchGame(className, name));

        card.add(topPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        // Card Hover Effekt
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(CARD_HOVER_COLOR);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(accentColor, 2),
                        BorderFactory.createEmptyBorder(19, 19, 19, 19)));
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_COLOR);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 80), 1),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)));
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                launchGame(className, name);
            }
        });

        return card;
    }

    // Hilfsmethode um semi-transparente Panels korrekt zu zeichnen
    private class TransparentPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g);
        }
    }

    private void launchGame(String className, String gameName) {
        try {
            // Versuche die Klasse zu laden und zu starten
            Class<?> gameClass = Class.forName(className);

            // Prüfe ob es eine main-Methode gibt
            java.lang.reflect.Method mainMethod = gameClass.getMethod("main", String[].class);

            // Starte in neuem Thread um UI nicht zu blockieren
            SwingUtilities.invokeLater(() -> {
                try {
                    mainMethod.invoke(null, (Object) new String[] {});
                } catch (Exception e) {
                    showError("Fehler beim Starten von " + gameName, e);
                }
            });

        } catch (ClassNotFoundException e) {
            // Klasse nicht gefunden - versuche zu kompilieren
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Das Spiel \"" + gameName + "\" muss erst kompiliert werden.\nJetzt kompilieren?",
                    "Spiel nicht gefunden",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                compileAndLaunch(className, gameName);
            }
        } catch (NoSuchMethodException e) {
            showError("Das Spiel \"" + gameName + "\" hat keine main-Methode.", e);
        } catch (Exception e) {
            showError("Fehler beim Starten von " + gameName, e);
        }
    }

    private void compileAndLaunch(String className, String gameName) {
        try {
            // Kompiliere das Spiel
            ProcessBuilder pb = new ProcessBuilder("javac", "-encoding", "UTF-8", className + ".java");
            pb.directory(new File("."));
            pb.redirectErrorStream(true);
            Process process = pb.start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                // Erfolgreich kompiliert, jetzt starten
                launchGame(className, gameName);
            } else {
                // Kompilierungsfehler
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                showError("Kompilierungsfehler:\n" + output.toString(), null);
            }
        } catch (Exception e) {
            showError("Fehler beim Kompilieren von " + gameName, e);
        }
    }

    private void showError(String message, Exception e) {
        String fullMessage = message;
        if (e != null) {
            fullMessage += "\n\nDetails: " + e.getMessage();
        }
        JOptionPane.showMessageDialog(
                this,
                fullMessage,
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(BACKGROUND_COLOR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel footerLabel = new JLabel("🕹️ Mehr Spiele kommen bald! 🕹️");
        footerLabel.setFont(new Font("Segoe UI Emoji", Font.ITALIC, 14));
        footerLabel.setForeground(TEXT_SECONDARY);

        footerPanel.add(footerLabel);

        return footerPanel;
    }

    public static void main(String[] args) {
        // Setze Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Starte die Arcade im EDT
        SwingUtilities.invokeLater(() -> {
            new Arcade();
        });
    }

    // Pulsierendes Label für den Titel
    class GlowLabel extends JLabel {
        private float phase = 0;

        public GlowLabel(String text) {
            super(text);
            new Timer(50, e -> {
                phase += 0.1f;
                repaint();
            }).start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            float pulse = (float) (Math.sin(phase) * 0.5 + 0.5);
            int glowSize = 8 + (int) (pulse * 10);

            // Glow Effekt
            g2.setColor(new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue(),
                    (int) (pulse * 100)));
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            for (int i = 0; i < glowSize; i++) {
                g2.drawString(getText(), x, y);
            }

            g2.setColor(getForeground());
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    // Innere Klasse für den Hintergrund mit Bäumen
    class BackgroundPanel extends JPanel {
        private java.util.List<int[]> trees; // x, height, width, type
        private java.util.List<float[]> stars; // x, y, size, alpha, alphaStep
        private java.util.List<Point> fairyLights;
        private float pulsePhase = 0;
        private float moonX = 50;
        private javax.swing.Timer animTimer;

        public BackgroundPanel() {
            setOpaque(false);
            generateBackground();

            // Animation für Lichter und Hintergrund
            animTimer = new javax.swing.Timer(40, e -> {
                pulsePhase += 0.05f;
                moonX += 0.05f;
                if (moonX > getWidth() + 100)
                    moonX = -100;

                // Sterne twinkeln
                for (float[] star : stars) {
                    star[3] += star[4];
                    if (star[3] > 1.0f || star[3] < 0.2f)
                        star[4] *= -1;
                }

                repaint();
            });
            animTimer.start();
        }

        private void generateBackground() {
            trees = new java.util.ArrayList<>();
            stars = new java.util.ArrayList<>();
            fairyLights = new java.util.ArrayList<>();
            java.util.Random rand = new java.util.Random(42);

            // Sterne generieren
            for (int i = 0; i < 100; i++) {
                stars.add(new float[] {
                        rand.nextFloat() * 1000,
                        rand.nextFloat() * 400,
                        rand.nextFloat() * 2 + 1,
                        rand.nextFloat(), // alpha
                        0.01f + rand.nextFloat() * 0.02f // alphaStep
                });
            }

            // Bäume im Hintergrund
            for (int i = 0; i < 12; i++) {
                trees.add(
                        new int[] { 50 + i * 70 + rand.nextInt(30), 80 + rand.nextInt(40), 25 + rand.nextInt(15), 0 });
            }

            // Bäume im Vordergrund
            for (int i = 0; i < 8; i++) {
                int x = 30 + i * 110 + rand.nextInt(50);
                int h = 150 + rand.nextInt(60);
                int w = 45 + rand.nextInt(25);
                trees.add(new int[] { x, h, w, 1 });

                // Lichter am Baum generieren
                for (int j = 0; j < 6; j++) {
                    fairyLights.add(new Point(x - w / 4 + rand.nextInt(w / 2), 600 - 50 - h / 2 - rand.nextInt(h / 2)));
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Gradient Nachthimmel
            GradientPaint sky = new GradientPaint(0, 0, new Color(10, 10, 35), 0, h, new Color(25, 35, 60));
            g2d.setPaint(sky);
            g2d.fillRect(0, 0, w, h);

            // Sterne zeichnen
            for (float[] star : stars) {
                int alpha = Math.max(0, Math.min(255, (int) (star[3] * 255)));
                g2d.setColor(new Color(255, 255, 255, alpha));
                int size = (int) star[2];
                g2d.fillOval((int) star[0], (int) star[1], size, size);
            }

            // Mond (bewegt sich)
            int mx = (int) moonX;
            g2d.setColor(new Color(255, 255, 220, 40));
            g2d.fillOval(mx - 5, 25, 90, 90);
            g2d.setColor(new Color(255, 255, 200, 180));
            g2d.fillOval(mx, 30, 80, 80);

            // Mondkrater-Effekt
            g2d.setColor(new Color(200, 200, 150, 60));
            g2d.fillOval(mx + 20, 50, 15, 15);
            g2d.fillOval(mx + 45, 45, 10, 10);
            g2d.fillOval(mx + 35, 70, 20, 20);

            // Bäume hinten
            for (int[] tree : trees) {
                if (tree[3] == 0)
                    drawTree(g2d, tree[0], h - 80, tree[1], tree[2], true);
            }

            // Lichterketten zwischen Bäumen (nur im Vordergrund)
            drawFairyChain(g2d, h);

            // Bäume vorne
            for (int[] tree : trees) {
                if (tree[3] == 1)
                    drawTree(g2d, tree[0], h - 50, tree[1], tree[2], false);
            }

            // Lichter an den Bäumen
            drawFairyLights(g2d);

            // Boden
            g2d.setColor(new Color(20, 30, 20));
            g2d.fillRect(0, h - 50, w, 50);

            // Gras-Effekt
            g2d.setColor(new Color(30, 50, 30));
            for (int x = 0; x < w; x += 8) {
                g2d.fillPolygon(new int[] { x, x + 4, x + 8 }, new int[] { h - 50, h - 58, h - 50 }, 3);
            }

            super.paintComponent(g);
        }

        private void drawFairyChain(Graphics2D g, int h) {
            g.setStroke(new BasicStroke(1.5f));
            g.setColor(new Color(20, 20, 20));

            java.util.List<int[]> frontTrees = new java.util.ArrayList<>();
            for (int[] t : trees)
                if (t[3] == 1)
                    frontTrees.add(t);

            for (int i = 0; i < frontTrees.size() - 1; i++) {
                int[] t1 = frontTrees.get(i);
                int[] t2 = frontTrees.get(i + 1);

                int x1 = t1[0];
                int y1 = h - 50 - t1[1] + 30;
                int x2 = t2[0];
                int y2 = h - 50 - t2[1] + 30;

                // Kurve zeichnen
                java.awt.geom.QuadCurve2D q = new java.awt.geom.QuadCurve2D.Float();
                q.setCurve(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2 + 40, x2, y2);
                g.draw(q);

                // Lichter an der Kette
                for (float t = 0.1f; t < 0.9f; t += 0.2f) {
                    double lx = (1 - t) * (1 - t) * x1 + 2 * (1 - t) * t * ((x1 + x2) / 2.0) + t * t * x2;
                    double ly = (1 - t) * (1 - t) * y1 + 2 * (1 - t) * t * ((y1 + y2) / 2.0 + 40) + t * t * y2;
                    drawSingleLight((int) lx, (int) ly, g, t);
                }
            }
        }

        private void drawFairyLights(Graphics2D g) {
            int i = 0;
            for (Point p : fairyLights) {
                drawSingleLight(p.x, p.y, g, i++ * 0.1f);
            }
        }

        private void drawSingleLight(int x, int y, Graphics2D g, float offset) {
            float pulse = (float) Math.sin(pulsePhase + offset) * 0.5f + 0.5f;
            Color[] colors = { new Color(255, 200, 50), new Color(255, 100, 100), new Color(100, 200, 255),
                    new Color(150, 255, 150) };
            Color baseColor = colors[(int) (offset * 10) % colors.length];

            // Glow
            float radius = 5 + pulse * 8;
            float[] dist = { 0.0f, 1.0f };
            Color[] colorsGlow = { new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 120),
                    new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 0) };
            RadialGradientPaint gradient = new RadialGradientPaint(x, y, radius, dist, colorsGlow);
            g.setPaint(gradient);
            g.fillOval((int) (x - radius), (int) (y - radius), (int) (radius * 2), (int) (radius * 2));

            // Core
            g.setColor(Color.WHITE);
            g.fillOval(x - 2, y - 2, 4, 4);
        }

        private void drawTree(Graphics2D g, int x, int groundY, int height, int width, boolean isFar) {
            if (!isFar) {
                g.setColor(new Color(0, 0, 0, 30));
                g.fillOval(x - width / 2 + 10, groundY - 5, width, 15);
            }

            int trunkWidth = width / 5;
            int trunkHeight = height / 3;
            Color trunkColor = isFar ? new Color(60, 40, 30) : new Color(80, 50, 35);
            g.setColor(trunkColor);
            g.fillRoundRect(x - trunkWidth / 2, groundY - trunkHeight, trunkWidth, trunkHeight, 3, 3);

            int crownY = groundY - trunkHeight;
            Color[] greens = isFar ? new Color[] { new Color(20, 45, 25), new Color(25, 55, 30), new Color(30, 65, 35) }
                    : new Color[] { new Color(25, 60, 35), new Color(35, 80, 45), new Color(45, 100, 55) };

            // Realistischere, rundere Baumkrone mit Ovale
            for (int i = 0; i < 3; i++) {
                g.setColor(greens[i]);
                int layerW = (int) (width * (1.1 - i * 0.2));
                int layerH = (int) (height * (0.6 - i * 0.1));
                g.fillOval(x - layerW / 2, crownY - layerH + (i * height / 10), layerW, layerH);
            }

            if (!isFar) {
                g.setColor(new Color(255, 255, 255, 40));
                g.fillOval(x - width / 4, crownY - height / 2, width / 4, height / 8);
            }
        }
    }
}
