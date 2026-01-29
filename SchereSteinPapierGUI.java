import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class SchereSteinPapierGUI extends JFrame {
    private JLabel resultLabel;
    private JLabel pcChoiceLabel;
    private String[] possibilities = { "Schere", "Stein", "Papier" };
    private String[] emojis = { "✂️", "🪨", "📜" };
    private Random random = new Random();

    public SchereSteinPapierGUI() {
        setTitle("✂️ Schere Stein Papier");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(new Color(15, 23, 42));

        JLabel title = new JLabel("Wähle deine Geste!", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        for (int i = 0; i < 3; i++) {
            final int index = i;
            JButton btn = createGameButton(emojis[i], possibilities[i]);
            btn.addActionListener(e -> play(possibilities[index]));
            buttonPanel.add(btn);
        }
        add(buttonPanel, BorderLayout.CENTER);

        JPanel resultPanel = new JPanel(new GridLayout(2, 1));
        resultPanel.setOpaque(false);
        pcChoiceLabel = new JLabel("Der PC wartet...", SwingConstants.CENTER);
        pcChoiceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pcChoiceLabel.setForeground(new Color(148, 163, 184));

        resultLabel = new JLabel("Viel Glück!", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        resultLabel.setForeground(new Color(99, 102, 241));

        resultPanel.add(pcChoiceLabel);
        resultPanel.add(resultLabel);
        resultPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(resultPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton createGameButton(String emoji, String text) {
        JButton btn = new JButton("<html><center><font size='6'>" + emoji + "</font><br>" + text + "</center></html>");
        btn.setBackground(new Color(30, 41, 59));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85), 2));
        return btn;
    }

    private void play(String userChoice) {
        int pcIndex = random.nextInt(3);
        String pcChoice = possibilities[pcIndex];
        String pcEmoji = emojis[pcIndex];

        pcChoiceLabel.setText("PC hat: " + pcEmoji + " " + pcChoice);

        if (userChoice.equals(pcChoice)) {
            resultLabel.setText("Unentschieden!");
            resultLabel.setForeground(Color.YELLOW);
        } else if ((userChoice.equals("Schere") && pcChoice.equals("Papier")) ||
                (userChoice.equals("Stein") && pcChoice.equals("Schere")) ||
                (userChoice.equals("Papier") && pcChoice.equals("Stein"))) {
            resultLabel.setText("Du hast gewonnen! 🎉");
            resultLabel.setForeground(new Color(34, 197, 94));
        } else {
            resultLabel.setText("PC hat gewonnen! 😢");
            resultLabel.setForeground(new Color(239, 68, 68));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SchereSteinPapierGUI::new);
    }
}
