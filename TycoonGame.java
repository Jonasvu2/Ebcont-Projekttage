import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Business Tycoon - Klicke dich zum Erfolg!
 * Werde vom einfachen Praktikanten zum Tech-Giganten.
 */
public class TycoonGame extends JFrame {

    private static final Color CARD_COLOR = new Color(30, 41, 59, 230);
    private static final Color ACCENT_COLOR = new Color(129, 140, 248); // Indigo
    private static final Color MONEY_COLOR = new Color(34, 197, 94); // Green
    private static final Color TEXT_COLOR = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);

    // Spiel-Variablen
    private double money = 0;
    private double moneyPerClick = 1;
    private double moneyPerSecond = 0;

    // Upgrades: {Name, Basis-Preis, Einkommen, Menge, Icon}
    private List<Upgrade> upgrades = new ArrayList<>();

    // UI Komponenten
    private JLabel moneyLabel;
    private JLabel mpsLabel;
    private JPanel upgradesPanel;
    private BackgroundPanel backgroundPanel;
    private Timer gameTimer;
    private static final Random random = new Random();

    public TycoonGame() {
        setTitle("🪐 Galactic Tycoon: Planet Harvest");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setPreferredSize(new Dimension(1000, 750));

        initUpgrades();
        initializeUI();
        startGameLoop();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initUpgrades() {
        upgrades.add(new Upgrade("Mond", 15, 0.5, "🌙", new Color(200, 200, 210)));
        upgrades.add(new Upgrade("Merkur", 100, 3, "🌡️", new Color(165, 165, 165)));
        upgrades.add(new Upgrade("Venus", 500, 12, "🌪️", new Color(255, 198, 107)));
        upgrades.add(new Upgrade("Mars", 1500, 35, "🔴", new Color(248, 113, 113)));
        upgrades.add(new Upgrade("Jupiter", 6000, 95, "🌀", new Color(251, 191, 36)));
        upgrades.add(new Upgrade("Saturn", 20000, 320, "🪐", new Color(216, 180, 133)));
        upgrades.add(new Upgrade("Uranus", 80000, 1100, "💎", new Color(165, 243, 252)));
        upgrades.add(new Upgrade("Neptun", 300000, 3000, "🌊", new Color(96, 165, 250)));
        upgrades.add(new Upgrade("Pluto", 1000000, 15000, "🌌", new Color(192, 132, 252)));

        // Neue Upgrades
        upgrades.add(new Upgrade("A-Gürtel", 3500000, 45000, "☄️", new Color(120, 100, 90)));
        upgrades.add(new Upgrade("Komet", 12000000, 140000, "🌠", new Color(200, 230, 255)));
        upgrades.add(new Upgrade("Station", 45000000, 500000, "🛰️", new Color(220, 220, 230)));
        upgrades.add(new Upgrade("Roter Zwerg", 150000000, 1800000, "🛑", new Color(255, 80, 80)));
        upgrades.add(new Upgrade("Supernova", 600000000, 7500000, "💥", new Color(255, 200, 50)));
        upgrades.add(new Upgrade("Nebula", 2500000000.0, 32000000, "🌫️", new Color(255, 100, 200)));
        upgrades.add(new Upgrade("Neutronen", 10000000000.0, 150000000, "✨", new Color(200, 240, 255)));
        upgrades.add(new Upgrade("Pulsar", 50000000000.0, 700000000, "⚡", new Color(100, 255, 255)));
        upgrades.add(new Upgrade("Schwarzes Loch", 250000000000.0, 4000000000.0, "🕳️", new Color(50, 0, 80)));
        upgrades.add(new Upgrade("Multiversum", 1000000000000.0, 20000000000.0, "🌌", new Color(100, 50, 255)));
    }

    private void initializeUI() {
        backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout(20, 20));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Links: Klick-Bereich
        backgroundPanel.add(createClickArea(), BorderLayout.CENTER);

        // Rechts: Upgrades
        backgroundPanel.add(createUpgradeArea(), BorderLayout.EAST);

        setContentPane(backgroundPanel);
    }

    private JPanel createClickArea() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Stats Panel (Glass Look) ---
        JPanel statsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 41, 59, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        statsPanel.setOpaque(false);
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        statsPanel.setMaximumSize(new Dimension(400, 120));

        moneyLabel = new JLabel("$ 0.00");
        moneyLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        moneyLabel.setForeground(MONEY_COLOR);
        moneyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mpsLabel = new JLabel("Einkommen: $ 0.00 / sek");
        mpsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        mpsLabel.setForeground(TEXT_SECONDARY);
        mpsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statsPanel.add(moneyLabel);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(mpsLabel);

        // --- Click Button (Smaller & Prettier) ---
        JButton clickButton = new JButton() {
            private float pulse = 0;
            private boolean growing = true;

            {
                Timer t = new Timer(50, e -> {
                    if (growing) {
                        pulse += 0.05f;
                        if (pulse >= 1)
                            growing = false;
                    } else {
                        pulse -= 0.05f;
                        if (pulse <= 0)
                            growing = true;
                    }
                    repaint();
                });
                t.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int d = Math.min(getWidth(), getHeight()) - 20;
                int x = (getWidth() - d) / 2;
                int y = (getHeight() - d) / 2;

                // Outer Glow
                float glowSize = 10 + pulse * 10;
                g2.setColor(new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue(), 60));
                g2.fillOval((int) (x - glowSize / 2), (int) (y - glowSize / 2), (int) (d + glowSize),
                        (int) (d + glowSize));

                // Planet Body
                g2.setColor(ACCENT_COLOR);
                g2.fillOval(x, y, d, d);

                // Shine effect
                g2.setPaint(
                        new GradientPaint(x, y, new Color(255, 255, 255, 100), x + d, y + d, new Color(0, 0, 0, 0)));
                g2.fillOval(x, y, d, d);

                // Icon
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
                FontMetrics fm = g2.getFontMetrics();
                String text = "🪐";
                g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, (getHeight() + fm.getAscent()) / 2 - 10);

                g2.dispose();
            }
        };
        clickButton.setPreferredSize(new Dimension(180, 180));
        clickButton.setMaximumSize(new Dimension(180, 180));
        clickButton.setContentAreaFilled(false);
        clickButton.setBorderPainted(false);
        clickButton.setFocusPainted(false);
        clickButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clickButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        clickButton.addActionListener(e -> {
            addMoney(moneyPerClick);
            spawnFloatingText(clickButton);
        });

        // --- Navigation ---
        JButton backBtn = new JButton("⬅ Menü");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setForeground(TEXT_SECONDARY);
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> dispose());

        panel.add(Box.createVerticalStrut(20));
        panel.add(statsPanel);
        panel.add(Box.createVerticalGlue());
        panel.add(clickButton);
        panel.add(Box.createVerticalGlue());
        panel.add(backBtn);
        panel.add(Box.createVerticalStrut(20));

        return panel;
    }

    private JPanel createUpgradeArea() {
        TransparentPanel panel = new TransparentPanel();
        panel.setPreferredSize(new Dimension(350, 0));
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("UPGRADES");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        upgradesPanel = new JPanel();
        upgradesPanel.setLayout(new BoxLayout(upgradesPanel, BoxLayout.Y_AXIS));
        upgradesPanel.setOpaque(false);

        for (Upgrade u : upgrades) {
            upgradesPanel.add(createUpgradeCard(u));
            upgradesPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scroll = new JScrollPane(upgradesPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUpgradeCard(Upgrade u) {
        JPanel card = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Glass effect darker
                g2.setColor(new Color(20, 25, 35, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                // Subtle border
                g2.setColor(new Color(255, 255, 255, 15));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        card.setMaximumSize(new Dimension(500, 75));

        // Icon (White Silhouette)
        JComponent iconLabel = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(u.color); // Use planet specific color

                Font font = new Font("Segoe UI Emoji", Font.PLAIN, 28);
                g2.setFont(font);

                FontMetrics fm = g2.getFontMetrics();
                int x = 0; // Align left
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

                java.awt.font.GlyphVector gv = font.createGlyphVector(g2.getFontRenderContext(), u.icon);
                Shape shape = gv.getOutline(x, y);
                g2.fill(shape);

                g2.dispose();
            }
        };
        iconLabel.setPreferredSize(new Dimension(50, 40));
        iconLabel.setOpaque(false);

        // Info
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(u.name + " (Level " + u.count + ")");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLabel.setForeground(TEXT_COLOR);

        JLabel rateLabel = new JLabel("+$ " + formatMoney(u.incomePerSec) + "/s");
        rateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rateLabel.setForeground(ACCENT_COLOR); // Highlight income

        textPanel.add(nameLabel);
        textPanel.add(rateLabel);

        // Buy Button
        JButton buyBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean canAfford = money >= u.currentPrice;
                Color base = canAfford ? u.color : new Color(60, 60, 70);

                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Text
                g2.setColor(canAfford ? Color.WHITE : new Color(150, 150, 160));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        buyBtn.setText("$ " + formatMoney(u.currentPrice));
        buyBtn.setPreferredSize(new Dimension(100, 35));
        buyBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        buyBtn.setContentAreaFilled(false);
        buyBtn.setBorderPainted(false);
        buyBtn.setFocusPainted(false);
        buyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buyBtn.addActionListener(e -> {
            if (money >= u.currentPrice) {
                money -= u.currentPrice;
                u.count++;
                u.currentPrice *= 1.15; // Preissteigerung
                moneyPerSecond += u.incomePerSec;

                // Add visual to background
                backgroundPanel.addOrbitObject(u.icon, u.color);

                updateUI();

                // Update Labels in der Card
                nameLabel.setText(u.name + " (Level " + u.count + ")");
                buyBtn.setText("$ " + formatMoney(u.currentPrice));
            }
        });

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        card.add(buyBtn, BorderLayout.EAST);

        return card;
    }

    private void addMoney(double amount) {
        money += amount;
        updateUI();
    }

    private void updateUI() {
        moneyLabel.setText("$ " + formatMoney(money));
        mpsLabel.setText("Einkommen: $ " + formatMoney(moneyPerSecond) + " / sek");
        upgradesPanel.repaint(); // Wichtig für Button-Farben
    }

    private String formatMoney(double amount) {
        if (amount >= 1_000_000_000_000.0)
            return String.format("%.2f T", amount / 1_000_000_000_000.0);
        if (amount >= 1_000_000_000.0)
            return String.format("%.2f B", amount / 1_000_000_000.0);
        if (amount >= 1_000_000.0)
            return String.format("%.2f M", amount / 1_000_000.0);
        if (amount >= 10_000.0)
            return String.format("%,.0f", amount);
        return String.format("%.2f", amount);
    }

    private void startGameLoop() {
        gameTimer = new Timer(100, e -> {
            if (moneyPerSecond > 0) {
                addMoney(moneyPerSecond / 10.0);
            }
        });
        gameTimer.start();
    }

    private void spawnFloatingText(JButton source) {
        JLabel floating = new JLabel("+$" + (int) moneyPerClick);
        floating.setFont(new Font("Segoe UI", Font.BOLD, 24));
        floating.setForeground(ACCENT_COLOR);
        floating.setSize(100, 30);

        Point p = source.getLocation();
        int x = p.x + source.getWidth() / 2 + random.nextInt(60) - 30;
        int y = p.y + source.getHeight() / 2 + random.nextInt(40) - 20;
        floating.setLocation(x, y);

        getContentPane().add(floating);
        getContentPane().setComponentZOrder(floating, 0);

        Timer anim = new Timer(20, null);
        final int[] frame = { 0 };
        anim.addActionListener(e -> {
            floating.setLocation(floating.getX(), floating.getY() - 2);
            float alpha = 1.0f - (frame[0] / 30f);
            if (alpha < 0) {
                getContentPane().remove(floating);
                anim.stop();
            } else {
                floating.setForeground(new Color(
                        ACCENT_COLOR.getRed(),
                        ACCENT_COLOR.getGreen(),
                        ACCENT_COLOR.getBlue(),
                        (int) (alpha * 255)));
            }
            frame[0]++;
        });
        anim.start();
    }

    // Hilfsklassen
    private class Upgrade {
        String name;
        double currentPrice;
        double incomePerSec;
        int count = 0;
        String icon;
        Color color;

        Upgrade(String name, double price, double income, String icon, Color color) {
            this.name = name;
            this.currentPrice = price;
            this.incomePerSec = income;
            this.icon = icon;
            this.color = color;
        }
    }

    class BackgroundPanel extends JPanel {
        private float angle = 0;
        private List<Point2D.Float> stars = new ArrayList<>();
        private List<Float> starSpeeds = new ArrayList<>();
        private List<OrbitObject> orbitObjects = new ArrayList<>();

        private class OrbitObject {
            String icon;
            Color color;
            float angle;
            float distance;
            float speed;
            float size;

            OrbitObject(String icon, Color color) {
                this.icon = icon;
                this.color = color;
                this.angle = random.nextFloat() * (float) Math.PI * 2;
                this.distance = 150 + random.nextFloat() * 400; // Radius
                this.speed = 0.002f + random.nextFloat() * 0.01f;
                // Random direction
                if (random.nextBoolean())
                    this.speed *= -1;
                this.size = 20 + random.nextFloat() * 25;
            }
        }

        public void addOrbitObject(String icon, Color color) {
            orbitObjects.add(new OrbitObject(icon, color));
        }

        public BackgroundPanel() {
            setOpaque(false);
            for (int i = 0; i < 100; i++) {
                stars.add(new Point2D.Float(random.nextFloat(), random.nextFloat()));
                starSpeeds.add(0.0005f + random.nextFloat() * 0.002f);
            }

            new Timer(50, e -> {
                angle += 0.01f;

                // Update stars
                for (int i = 0; i < stars.size(); i++) {
                    Point2D.Float s = stars.get(i);
                    s.y += starSpeeds.get(i);
                    if (s.y > 1.0f)
                        s.y = 0f;
                }

                // Update orbiting objects
                for (OrbitObject obj : orbitObjects) {
                    obj.angle += obj.speed;
                }

                repaint();
            }).start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // Deep Space Gradient
            Point2D center = new Point2D.Float(
                    (float) (w / 2f + Math.cos(angle) * 50),
                    (float) (h / 2f + Math.sin(angle) * 50));
            float radius = Math.max(w, h);
            RadialGradientPaint rgp = new RadialGradientPaint(center, radius,
                    new float[] { 0f, 0.5f, 1f },
                    new Color[] { new Color(25, 30, 60), new Color(15, 20, 30), new Color(5, 5, 10) });
            g2.setPaint(rgp);
            g2.fillRect(0, 0, w, h);

            // Draw Stars
            g2.setColor(Color.WHITE);
            for (Point2D.Float s : stars) {
                int sx = (int) (s.x * w);
                int sy = (int) (s.y * h);
                int size = (int) (1 + Math.sin(angle * 5 + sx) * 1.5); // Twinkle
                g2.fillOval(sx, sy, Math.max(1, size), Math.max(1, size));
            }

            // Draw Orbiting Objects
            int cx = w / 2;
            int cy = h / 2;

            for (OrbitObject obj : orbitObjects) {
                int ox = (int) (cx + Math.cos(obj.angle) * obj.distance);
                int oy = (int) (cy + Math.sin(obj.angle) * obj.distance);

                // Draw Icon
                // Draw Icon (as colored silhouette)
                g2.setColor(obj.color); // Use planet specific color
                Font font = new Font("Segoe UI Emoji", Font.PLAIN, (int) obj.size);
                g2.setFont(font);
                FontMetrics fm = g2.getFontMetrics();
                int tx = ox - fm.stringWidth(obj.icon) / 2;
                int ty = oy + fm.getAscent() / 2 - 2;

                // Render as shape to force white color
                java.awt.font.GlyphVector gv = font.createGlyphVector(g2.getFontRenderContext(), obj.icon);
                Shape shape = gv.getOutline(tx, ty);
                g2.fill(shape);
            }

            g2.dispose();
            super.paintComponent(g); // Draw children components on top
        }
    }

    class TransparentPanel extends JPanel {
        public TransparentPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CARD_COLOR);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        SwingUtilities.invokeLater(TycoonGame::new);
    }
}
