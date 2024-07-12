import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NQueen {
    private static final int CELL_SIZE = 60; // Size of each cell on the chessboard
    private static final int STEP_DELAY = 500; // Delay in milliseconds for each step
    private static JFrame frame;
    private static JLabel[][] labels;
    private static JLabel boardLabel;
    private static int n;

    public static void main(String[] args) {
        String input = JOptionPane.showInputDialog(null, "Enter the value of N:", "N-Queens Solver", JOptionPane.QUESTION_MESSAGE);
        try {
            n = Integer.parseInt(input);
            if (n < 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid positive integer.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setupBoard(n);
        new Thread(() -> {
            List<List<String>> chessboards = solveNQueens(n);
            printChessBoard(chessboards);
            displaySolutions(chessboards, n);
        }).start();
    }

    public static List<List<String>> solveNQueens(int n) {
        List<List<String>> output = new ArrayList<>();
        char[][] board = new char[n][n];

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = '0';
            }
        }
        nQueens(output, board, 0);
        return output;
    }

    public static void nQueens(List<List<String>> output, char[][] board, int row) {
        if (row == board.length) {
            addBoard(output, board);
            return;
        }

        for (int j = 0; j < board.length; j++) {
            if (isSafe(board, row, j)) {
                board[row][j] = '1';
                updateBoard(board);
                sleep(STEP_DELAY);
                nQueens(output, board, row + 1);
                board[row][j] = '0'; // backtrack
                updateBoard(board);
                sleep(STEP_DELAY);
            }
        }
    }

    public static void addBoard(List<List<String>> output, char[][] board) {
        ArrayList<String> bo = new ArrayList<>();

        for (int i = 0; i < board.length; i++) {
            StringBuilder str = new StringBuilder();
            for (int j = 0; j < board[i].length; j++) {
                str.append(board[i][j]);
            }
            bo.add(str.toString());
        }
        output.add(bo);
    }

    public static boolean isSafe(char[][] board, int row, int col) {
        for (int i = row - 1; i >= 0; i--) {
            if (board[i][col] == '1')
                return false;
        }

        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (board[i][j] == '1')
                return false;
        }

        for (int i = row - 1, j = col + 1; i >= 0 && j < board.length; i--, j++) {
            if (board[i][j] == '1')
                return false;
        }
        return true;
    }

    public static void printChessBoard(List<List<String>> board) {
        for (int i = 0; i < board.get(0).size() + 4; i++) {
            System.out.print("-");
        }
        System.out.println();

        int j = 1;
        for (List<String> bd : board) {
            System.out.println("Board: " + j++);
            for (String ln : bd) {
                System.out.println("| " + ln + " |");
            }
            for (int i = 0; i < board.get(0).size() + 4; i++) {
                System.out.print("-");
            }
            System.out.println();
        }

        System.out.println("Possible Arrangements : " + board.size());
    }

    public static void displaySolutions(List<List<String>> solutions, int n) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("N-Queens Solutions");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(n * CELL_SIZE, n * CELL_SIZE + 80); // +80 for window decorations and label
            frame.setLayout(new BorderLayout());

            JLabel boardLabel = new JLabel("Board 1", SwingConstants.CENTER);
            boardLabel.setFont(new Font("Serif", Font.BOLD, 20));
            frame.add(boardLabel, BorderLayout.NORTH);

            JPanel boardPanel = new JPanel(new GridLayout(n, n));
            boardPanel.setPreferredSize(new Dimension(n * CELL_SIZE, n * CELL_SIZE));
            frame.add(boardPanel, BorderLayout.CENTER);

            JLabel[][] labels = new JLabel[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    labels[i][j] = new JLabel();
                    labels[i][j].setOpaque(true);
                    labels[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.BLACK);
                    labels[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                    labels[i][j].setVerticalAlignment(SwingConstants.CENTER);
                    labels[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    boardPanel.add(labels[i][j]);
                }
            }

            JButton nextButton = new JButton("Next Solution");
            frame.add(nextButton, BorderLayout.SOUTH);

            final int[] solutionIndex = {0};
            nextButton.addActionListener(e -> {
                if (solutions.size() > 0) {
                    solutionIndex[0] = (solutionIndex[0] + 1) % solutions.size();
                    displaySolution(labels, solutions.get(solutionIndex[0]), n);
                    boardLabel.setText("Board " + (solutionIndex[0] + 1));
                }
            });

            if (solutions.size() > 0) {
                displaySolution(labels, solutions.get(0), n);
            }

            frame.setVisible(true);
        });
    }

    private static void displaySolution(JLabel[][] labels, List<String> solution, int n) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (solution.get(i).charAt(j) == '1') {
                    labels[i][j].setText("Q");
                    labels[i][j].setForeground(Color.RED);
                } else {
                    labels[i][j].setText("");
                }
            }
        }
    }

    private static void setupBoard(int n) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("N-Queens Solver");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(n * CELL_SIZE, n * CELL_SIZE + 80); // +80 for window decorations and label
            frame.setLayout(new BorderLayout());

            boardLabel = new JLabel("Solving...", SwingConstants.CENTER);
            boardLabel.setFont(new Font("Serif", Font.BOLD, 20));
            frame.add(boardLabel, BorderLayout.NORTH);

            JPanel boardPanel = new JPanel(new GridLayout(n, n));
            boardPanel.setPreferredSize(new Dimension(n * CELL_SIZE, n * CELL_SIZE));
            frame.add(boardPanel, BorderLayout.CENTER);

            labels = new JLabel[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    labels[i][j] = new JLabel();
                    labels[i][j].setOpaque(true);
                    labels[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.BLACK);
                    labels[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                    labels[i][j].setVerticalAlignment(SwingConstants.CENTER);
                    labels[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    boardPanel.add(labels[i][j]);
                }
            }

            frame.setVisible(true);
        });
    }

    private static void updateBoard(char[][] board) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (board[i][j] == '1') {
                        labels[i][j].setText("Q");
                        labels[i][j].setForeground(Color.RED);
                    } else {
                        labels[i][j].setText("");
                    }
                }
            }
        });
    }

    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
