import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToeGUI extends JFrame {
    private JButton[][] boardButtons = new JButton[3][3];
    private boolean xTurn = true;
    private boolean gameOver = false;
    private JLabel statusLabel;
    private JPanel boardPanel;
    private final Color BACKGROUND_COLOR = new Color(15, 23, 42);
    private final Color BUTTON_COLOR = new Color(30, 41, 59);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color X_COLOR = new Color(99, 102, 241); // Indigo
    private final Color O_COLOR = new Color(239, 68, 68); // Red
    private final Color WIN_COLOR = new Color(34, 197, 94); // Green

    public TicTacToeGUI() {
        setTitle("🎮 Tic Tac Toe - Pro Edition");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel titleLabel = new JLabel("Tic Tac Toe", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Board
        boardPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        boardPanel.setBackground(BACKGROUND_COLOR);
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                boardButtons[row][col] = createBoardButton(row, col);
                boardPanel.add(boardButtons[row][col]);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        // Footer / Status
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(BACKGROUND_COLOR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        statusLabel = new JLabel("Player X's Turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        statusLabel.setForeground(X_COLOR);
        footerPanel.add(statusLabel, BorderLayout.NORTH);

        JButton resetBtn = new JButton("Reset Game");
        resetBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resetBtn.setBackground(new Color(51, 65, 85));
        resetBtn.setForeground(TEXT_COLOR);
        resetBtn.setFocusPainted(false);
        resetBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        resetBtn.addActionListener(e -> resetGame());
        footerPanel.add(resetBtn, BorderLayout.SOUTH);

        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton createBoardButton(int row, int col) {
        JButton btn = new JButton("");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 60));
        btn.setBackground(BUTTON_COLOR);
        btn.setForeground(TEXT_COLOR);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85), 2));

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btn.getText().equals("") && !gameOver) {
                    if (xTurn) {
                        btn.setText("X");
                        btn.setForeground(X_COLOR);
                        statusLabel.setText("Player O's Turn");
                        statusLabel.setForeground(O_COLOR);
                    } else {
                        btn.setText("O");
                        btn.setForeground(O_COLOR);
                        statusLabel.setText("Player X's Turn");
                        statusLabel.setForeground(X_COLOR);
                    }
                    xTurn = !xTurn;

                    if (checkWinner()) {
                        gameOver = true;
                        String winner = xTurn ? "O" : "X";
                        statusLabel.setText("Player " + winner + " wins! 🎉");
                        statusLabel.setForeground(WIN_COLOR);
                        highlightWinningButtons();
                    } else if (isBoardFull()) {
                        gameOver = true;
                        statusLabel.setText("It's a Draw! 🤝");
                        statusLabel.setForeground(Color.YELLOW);
                    }
                }
            }
        });

        return btn;
    }

    private boolean checkWinner() {
        // Rows
        for (int i = 0; i < 3; i++) {
            if (!boardButtons[i][0].getText().equals("") &&
                    boardButtons[i][0].getText().equals(boardButtons[i][1].getText()) &&
                    boardButtons[i][0].getText().equals(boardButtons[i][2].getText())) {
                return true;
            }
        }
        // Columns
        for (int i = 0; i < 3; i++) {
            if (!boardButtons[0][i].getText().equals("") &&
                    boardButtons[0][i].getText().equals(boardButtons[1][i].getText()) &&
                    boardButtons[0][i].getText().equals(boardButtons[2][i].getText())) {
                return true;
            }
        }
        // Diagonals
        if (!boardButtons[0][0].getText().equals("") &&
                boardButtons[0][0].getText().equals(boardButtons[1][1].getText()) &&
                boardButtons[0][0].getText().equals(boardButtons[2][2].getText())) {
            return true;
        }
        if (!boardButtons[0][2].getText().equals("") &&
                boardButtons[0][2].getText().equals(boardButtons[1][1].getText()) &&
                boardButtons[0][2].getText().equals(boardButtons[2][0].getText())) {
            return true;
        }
        return false;
    }

    private void highlightWinningButtons() {
        // Rows
        for (int i = 0; i < 3; i++) {
            if (!boardButtons[i][0].getText().equals("") &&
                    boardButtons[i][0].getText().equals(boardButtons[i][1].getText()) &&
                    boardButtons[i][0].getText().equals(boardButtons[i][2].getText())) {
                setWinnerStyle(boardButtons[i][0], boardButtons[i][1], boardButtons[i][2]);
            }
        }
        // Columns
        for (int i = 0; i < 3; i++) {
            if (!boardButtons[0][i].getText().equals("") &&
                    boardButtons[0][i].getText().equals(boardButtons[1][i].getText()) &&
                    boardButtons[0][i].getText().equals(boardButtons[2][i].getText())) {
                setWinnerStyle(boardButtons[0][i], boardButtons[1][i], boardButtons[2][i]);
            }
        }
        // Diagonals
        if (!boardButtons[0][0].getText().equals("") &&
                boardButtons[0][0].getText().equals(boardButtons[1][1].getText()) &&
                boardButtons[0][0].getText().equals(boardButtons[2][2].getText())) {
            setWinnerStyle(boardButtons[0][0], boardButtons[1][1], boardButtons[2][2]);
        }
        if (!boardButtons[0][2].getText().equals("") &&
                boardButtons[0][2].getText().equals(boardButtons[1][1].getText()) &&
                boardButtons[0][2].getText().equals(boardButtons[2][0].getText())) {
            setWinnerStyle(boardButtons[0][2], boardButtons[1][1], boardButtons[2][0]);
        }
    }

    private void setWinnerStyle(JButton... buttons) {
        for (JButton b : buttons) {
            b.setBackground(new Color(22, 163, 74, 100)); // Faded green background
            b.setBorder(BorderFactory.createLineBorder(WIN_COLOR, 3));
        }
    }

    private boolean isBoardFull() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (boardButtons[row][col].getText().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    private void resetGame() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                boardButtons[row][col].setText("");
                boardButtons[row][col].setBackground(BUTTON_COLOR);
                boardButtons[row][col].setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85), 2));
            }
        }
        xTurn = true;
        gameOver = false;
        statusLabel.setText("Player X's Turn");
        statusLabel.setForeground(X_COLOR);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        SwingUtilities.invokeLater(TicTacToeGUI::new);
    }
}
