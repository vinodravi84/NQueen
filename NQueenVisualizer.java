import javax.swing.*;
import java.awt.*;

public class NQueenVisualizer {
    public static void main(String[] args) {
        JFrame frame = new JFrame("N-Queen Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        
        NQueenPanel panel = new NQueenPanel(5); // Change 4 to N for different board sizes
        frame.add(panel);
        frame.setVisible(true);

        new Thread(() -> {
            try {
                panel.placeQueens();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

class NQueenPanel extends JPanel {
    private static final Color COLOR_RED = new Color(200, 0, 0);   // Red for unsafe
    private static final Color COLOR_ORANGE = new Color(255, 165, 0); // Orange for causing trouble

    private int N;
    private int[] board;
    private Color[][] cellColors;
    private int currentRow;
    private int currentCol;

    public NQueenPanel(int N) {
        this.N = N;
        this.board = new int[N];
        this.cellColors = new Color[N][N];
        for (int i = 0; i < N; i++) {
            board[i] = -1;
            for (int j = 0; j < N; j++) {
                cellColors[i][j] = (i + j) % 2 == 0 ? Color.WHITE : Color.GRAY; // Alternating colors for chessboard pattern
            }
        }
        setPreferredSize(new Dimension(800, 800));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
    }

    private void drawBoard(Graphics g) {
        int cellSize = getWidth() / N;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                g.setColor(cellColors[i][j]);
                g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);

                if (board[i] == j) {
                    g.setColor(Color.BLACK);
                    g.fillOval(j * cellSize + cellSize / 4, i * cellSize + cellSize / 4, cellSize / 2, cellSize / 2);
                }
            }
        }
    }

    public void placeQueens() throws InterruptedException {
        placeQueensOnRow(0);
    }

    private void placeQueensOnRow(int row) throws InterruptedException {
        if (row == N) {
            return; // All queens placed successfully
        }

        for (int col = 0; col < N; col++) {
            board[row] = col;
            currentRow = row;
            currentCol = col;
            repaint();
            Thread.sleep(500); // Delay for visualization

            if (isSafe(row, col)) {
                placeQueensOnRow(row + 1);
            } else {
                // Highlight unsafe positions in red and orange
                for (int i = 0; i < N; i++) {
                    if (board[i] != -1) {
                        int j = board[i];
                        // Straight column
                        if (j == col) {
                            cellColors[i][j] = COLOR_ORANGE;
                        }
                        // Diagonal positions
                        if (Math.abs(row - i) == Math.abs(col - j)) {
                            cellColors[i][j] = COLOR_ORANGE;
                        }
                    }
                }
                repaint();
                Thread.sleep(1500); // Delay for visualization

                // Clear highlighted cells
                for (int i = 0; i < N; i++) {
                    if (board[i] != -1) {
                        int j = board[i];
                        // Straight column
                        if (j == col) {
                            cellColors[i][j] = (i + j) % 2 == 0 ? Color.WHITE : Color.GRAY;
                        }
                        // Diagonal positions
                        if (Math.abs(row - i) == Math.abs(col - j)) {
                            cellColors[i][j] = (i + j) % 2 == 0 ? Color.WHITE : Color.GRAY;
                        }
                    }
                }

                // Highlight current cell as unsafe in red
                cellColors[row][col] = COLOR_RED;
                repaint();
                Thread.sleep(1000); // Delay for visualization

                cellColors[row][col] = (row + col) % 2 == 0 ? Color.WHITE : Color.GRAY; // Revert to original chessboard color
                board[row] = -1; // Clear board position
                repaint();
                Thread.sleep(500); // Delay for visualization
            }
        }
    }

    private boolean isSafe(int row, int col) {
        for (int i = 0; i < row; i++) {
            int j = board[i];
            if (j == col || Math.abs(j - col) == Math.abs(i - row)) {
                return false;
            }
        }
        return true;
    }
}
