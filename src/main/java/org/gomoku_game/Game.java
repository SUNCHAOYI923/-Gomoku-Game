package org.gomoku_game;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Game extends Application {

    private static final int BOARD_SIZE = 20; // 游戏棋盘大小 (20x20)
    private static final int CELL_SIZE = 40; // 每个格子的大小
    private static final String BLACK_PLAYER = "Black";
    private static final String WHITE_PLAYER = "White";

    private Label currentPlayerLabel = new Label("Current Player: " + BLACK_PLAYER);
    private Label winnerLabel = new Label("");
    private GridPane boardGrid = new GridPane();
    private Circle[][] circles = new Circle[BOARD_SIZE][BOARD_SIZE];
    private int movesCount = 0; // 当前回合数

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage nw) {
        nw.setTitle("Gomoku Game");

        // 创建主布局 (BorderPane)
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // 初始化棋盘
        initializeBoard();

        // 创建底部状态栏
        HBox statusBox = createStatus();

        // 设置布局
        root.setCenter(boardGrid);
        root.setBottom(statusBox);

        // 设置场景并显示窗口
        Scene scene = new Scene(root, (BOARD_SIZE * CELL_SIZE), (BOARD_SIZE * CELL_SIZE));
        nw.setScene(scene);
        nw.show();
    }

    private void initializeBoard() {
        // 设置棋盘背景颜色为灰色，并添加黑色边框
        boardGrid.setBackground(new javafx.scene.layout.Background(
                new javafx.scene.layout.BackgroundFill(Color.LIGHTGRAY, null, null)));

        // 添加黑色边框并确保边框不影响内部布局
        boardGrid.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-insets: 0;");

        // 设置 GridPane 的填充以匹配边框宽度
        boardGrid.setPadding(new Insets(2)); // 边框宽度为 2，所以设置内边距为 2

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Circle circle = new Circle(CELL_SIZE / 2 - 5, Color.TRANSPARENT); // 空白格子
                circles[row][col] = circle; // 保存引用以便后续访问
                int finalRow = row;
                int finalCol = col;

                // 添加点击事件
                circle.setOnMouseClicked(event -> handleCellClick(finalRow, finalCol));
                boardGrid.add(circle, col, row);

                // 设置格子间距
                boardGrid.setHgap(2);
                boardGrid.setVgap(2);
            }
        }

        // 调整 GridPane 的尺寸以适配棋盘格子
        boardGrid.setPrefSize((BOARD_SIZE * CELL_SIZE) + 4, (BOARD_SIZE * CELL_SIZE) + 4);
    }

    private HBox createStatus() {
        HBox statusBox = new HBox(10);
        statusBox.setPadding(new Insets(10));

        // 当前玩家和胜负信息
        statusBox.getChildren().addAll(currentPlayerLabel, winnerLabel);
        return statusBox;
    }

    private void handleCellClick(int row, int col) {
        if (!winnerLabel.getText().isEmpty()) {
            return; // 如果已经有赢家，则忽略点击
        }

        Circle circle = circles[row][col];
        if (circle.getFill() == Color.TRANSPARENT) {
            circle.setFill(currentPlayerLabel.getText().contains(BLACK_PLAYER) ? Color.BLACK : Color.WHITE);

            // 切换当前玩家
            String currentPlayer = currentPlayerLabel.getText().contains(BLACK_PLAYER) ? WHITE_PLAYER : BLACK_PLAYER;
            currentPlayerLabel.setText("Current Player: " + currentPlayer);

            movesCount++; // 增加回合数

            // 检查胜负逻辑
            if (checkWinner(row, col)) {
                disableBoard(); // 禁用棋盘
            }
        }
    }

    private boolean checkWinner(int row, int col) {
        Paint paint = circles[row][col].getFill();
        Color color = null;

        if (paint instanceof Color) {
            color = (Color) paint;
        } else {
            return false;
        }

        int[][] directions = {
                {0, 1},  // 水平方向
                {1, 0},  // 垂直方向
                {1, 1},  // 主对角线
                {1, -1}  // 反对角线
        };

        for (int[] dir : directions) {
            int count = 1; // 当前点算作一个棋子
            count += countInDirection(row, col, dir[0], dir[1], color);
            count += countInDirection(row, col, -dir[0], -dir[1], color);

            if (count >= 5) {
                winnerLabel.setText((color == Color.BLACK ? BLACK_PLAYER : WHITE_PLAYER) + " wins!");
                return true;
            }
        }

        return false;
    }

    private int countInDirection(int row, int col, int dRow, int dCol, Color color) {
        int count = 0;
        int r = row + dRow, c = col + dCol;

        while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && circles[r][c].getFill() == color) {
            count++;
            r += dRow;
            c += dCol;
        }

        return count;
    }

    private void disableBoard() {
        for (Node node : boardGrid.getChildren()) {
            node.setOnMouseClicked(null); // 移除点击事件
        }
    }

    private void resetGame() {
        // 重置棋盘
        for (Node node : boardGrid.getChildren()) {
            Circle circle = (Circle) node;
            circle.setFill(Color.TRANSPARENT);
            circle.setOnMouseClicked(event -> {
                int col = GridPane.getColumnIndex(circle);
                int row = GridPane.getRowIndex(circle);
                handleCellClick(row, col);
            });
        }

        // 重置状态
        currentPlayerLabel.setText("Current Player: " + BLACK_PLAYER);
        winnerLabel.setText("");
    }
}