package org.gomoku_game;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Stack;

import static java.lang.Math.max;

public class New_Game extends Application
{
    public static int Black_ti = 0,White_ti = 0;
    private static Label B_ti,W_ti,B_Moves,W_Moves,B_Max,W_Max;
    private static int b_cnt = 0,w_cnt = 0,b_mx = 0,w_mx = 0,tot = Constant.MAX_TIME,lim = Constant.MAX_SINGLE_TIME;
    public static int cur_ti = 0;
    public static boolean stop = false;
    public static boolean br = false;
    private static Stack <GameState> undoStack = new Stack <> ();
    private static Stack <GameState> redoStack = new Stack <> ();
    private static Button undoButton,redoButton,saveButton;
    public static ProgressBar white_bar,black_bar;
    private double per;

    static class GameState implements Serializable
    {
        int[][] board_st; //棋盘状态
        Side player; //当前玩家
        public GameState (int[][] boardState, Side currentPlayer) {this.board_st = copy_board (boardState);this.player = currentPlayer;}
        private int[][] copy_board (int[][] ori)
        {
            if (ori == null) return null;
            final int[][] nw = new int[ori.length][];
            for (int i = 0;i < ori.length;++i) nw[i] = Arrays.copyOf (ori[i], ori[i].length);
            return nw;
        }
    }

    @Override
    public void start (Stage nw)
    {
        white_bar = new ProgressBar (1.0);black_bar = new ProgressBar (1.0);
        per = 1.0 / lim;br = false;
        Gomoku gomoku = new Gomoku ();
        final Board board = new Board (gomoku);
        undoStack.clear ();redoStack.clear ();
        upd_st (Gomoku.getChess (),gomoku.getCurrentSide ());
        if (Start.org != null) // 如果存在游戏状态，则加载游戏状态
        {
            Gomoku.setChess (Start.org.board_st);
            Gomoku.setCurrentSide (Start.org.player);
            board.draw_chess (Constant.sz);
            if (Start.org.player == Side.BLACK) black_bar.setProgress (1.0 - cur_ti * per);
            else white_bar.setProgress (1.0 - cur_ti * per);
            Start.org = null;
        }
        else  {Black_ti = 0;White_ti = 0;}
        board.setOnMouseClicked (event ->
        {
            if (!board.isDisabled ()) // 检查是否被禁用
            {
                board.setDisable (true); //防止双击造成影响
                PlayAction playAction = new PlayAction (gomoku, board, Start.ty);
                playAction.handle (event);
                board.setDisable (false);
            }
        });

        Thread timerThread = new Thread (() ->
        {
            while (!br && Start.ty == 0) // 防止多个计数器同时工作 && AI 模式时候不启动
            {
                try
                {
                    Thread.sleep (1000);
                    if (!stop)
                    {
                        Platform.runLater(() ->
                        {
                            ++cur_ti;
                            Side currentSide = Gomoku.getCurrentSide ();
                            upd_bar (currentSide);
                            if (cur_ti >= lim) //超时切换玩家
                            {
                                switchPlayer ();
                                cur_ti = 0;
                                reset_bar (currentSide);
                            }
                        });
                    }
                }
                catch (InterruptedException e) {e.printStackTrace();}
            }
        });
        timerThread.setDaemon (true);timerThread.start ();

        BorderPane borderPane = new BorderPane ();
        borderPane.setCenter (board);
        VBox rightPanel = inf (nw);
        borderPane.setRight (rightPanel);
        Scene scene = new Scene (borderPane, Constant.width, Constant.height);
        nw.setScene (scene);nw.setTitle ("Gomoku Game");nw.centerOnScreen ();nw.show ();
        TIME ();upd_button ();
    }
    private void upd_bar (Side side)
    {
        if (side == Side.BLACK) black_bar.setProgress (black_bar.getProgress () - per);
        else white_bar.setProgress (white_bar.getProgress () - per);
    }

    private void reset_bar (Side side)
    {
        if (side == Side.BLACK) black_bar.setProgress (1.0);
        else white_bar.setProgress (1.0);
    }
    private void switchPlayer ()
    {
        Side nextSide = Gomoku.getCurrentSide () == Side.BLACK ? Side.WHITE : Side.BLACK;
        Gomoku.setCurrentSide (nextSide); // 切换当前玩家
        Platform.runLater (() ->
        {
            Alert alert = new Alert (Alert.AlertType.INFORMATION);
            alert.setTitle ("Gomoku Game");alert.setHeaderText (null);
            alert.setContentText ("The side has been changed due to timeout!");
            alert.showAndWait ();
        });
    }

    private VBox inf (Stage nw)
    {
        VBox container = new VBox (10);
        container.setPadding (new Insets (10));
        container.setStyle ("-fx-background-color: #ffffcc;");
        container.setPrefWidth (250);
        HBox top = new HBox (10),bottom = new HBox (10);
        top.setPadding (new Insets (5));bottom.setPadding (new Insets (5));
        saveButton = new Button ("Save");
        saveButton.setOnAction (event -> FILE.save_game (nw));
        Button pauseButton = new Button ("Pause");
        pauseButton.setOnAction (event -> Platform.runLater (() -> end ("Pause")));
        undoButton = new Button ("Undo");
        undoButton.setOnAction (event -> {undo ();upd_button ();}); // 禁用/启用按钮的切换
        redoButton = new Button ("Redo");
        redoButton.setOnAction (event -> {redo ();upd_button ();});
        Button quitButton = new Button ("Quit");
        quitButton.setOnAction (event -> {System.exit (0);});
        top.getChildren ().addAll (saveButton,pauseButton,quitButton);
        bottom.getChildren ().addAll (undoButton,redoButton);
        container.getChildren ().add (top);container.getChildren ().add (bottom);

        GridPane blackInfoPanel = new GridPane ();
        blackInfoPanel.setVgap (5);
        blackInfoPanel.setPadding (new Insets (5));
        Label blackPlayerInfo = new Label ("Black");
        B_ti = new Label ("Rest time : " + formatTime (tot - Black_ti));
        B_Moves = new Label("Moves: " + b_cnt);
        B_Max = new Label("Max Lenth: " + b_mx);

        blackInfoPanel.add(blackPlayerInfo, 0, 0);
        blackInfoPanel.add(B_Moves, 0, 1);
        blackInfoPanel.add(B_Max, 5, 1);
        blackInfoPanel.add (B_ti, 0, 2);
        blackInfoPanel.add (black_bar, 0, 3);
        container.getChildren ().add (blackInfoPanel);

        Separator separator = new Separator ();
        separator.setHalignment (HPos.CENTER);
        VBox.setMargin (separator, new Insets (10, 0, 10, 0)); // 设置分隔线上下的间距
        container.getChildren ().add (separator);

        GridPane whiteInfoPanel = new GridPane ();
        whiteInfoPanel.setVgap (5);
        whiteInfoPanel.setPadding (new Insets (5));
        Label whitePlayerInfo = new Label ("White");
        W_ti = new Label ("Rest time : " + formatTime (tot - White_ti));
        W_Moves = new Label("Moves: " + w_cnt);
        W_Max = new Label("Max Lenth: " + w_mx);
        whiteInfoPanel.add (whitePlayerInfo, 0, 0);
        whiteInfoPanel.add (W_Moves, 0, 1);
        whiteInfoPanel.add (W_Max, 5, 1);
        whiteInfoPanel.add (W_ti, 0, 2);
        whiteInfoPanel.add (white_bar, 0, 3);
        container.getChildren ().add (whiteInfoPanel);
        return container;
    }

    public static void upd_button () {Platform.runLater (() ->
    {
        // AI 在下棋时按钮全面禁用
        undoButton.setDisable (PlayAction.AI_turn || undoStack.size () <= 1);
        redoButton.setDisable (PlayAction.AI_turn || redoStack.empty ());
        saveButton.setDisable (PlayAction.AI_turn);
        display_info ();
    });}

    private static void display_info() // 双方棋子数量以及最大 lenth
    {
        b_cnt = 0;w_cnt = 0;b_mx = 0;w_mx = 0;
        for (int i = 0;i < Constant.LEN;++i)
        {
            for (int j = 0; j < Constant.LEN; ++j)
            {
                if (Gomoku.chess[i][j] == 0) {
                    ++b_cnt;
                    b_mx = max(b_mx, calc_mx(i, j, 0));
                }
                if (Gomoku.chess[i][j] == 1) {
                    ++w_cnt;
                    w_mx = max(w_mx, calc_mx(i, j, 1));
                }
            }
        }
        B_Moves.setText ("Moves: " + b_cnt); B_Max.setText ("Max Length: " + b_mx);
        W_Moves.setText ("Moves: " + w_cnt); W_Max.setText ("Max Length: " + w_mx);
    }

    private static int calc_mx (int row,int col,int role)
    {
        int cnt = 0,l = col,r = col,board[][] = Gomoku.getChess ();
        while (l >= 1 && board[row][l - 1] == role) --l;
        while (r < Constant.LEN - 1 && board[row][r + 1] == role) ++r;
        cnt = max (cnt,r - l + 1);
        l = row;r = row;
        while (l >= 1 && board[l - 1][col] == role) --l;
        while (r < Constant.LEN - 1 && board[r + 1][col] == role) ++r;
        cnt = max (cnt,r - l + 1);
        int x = row,y = col,tmp = 1;
        while (x >= 1 && y >= 1 && board[x - 1][y - 1] == role) {--x;--y;++tmp;}
        x = row;y = col;
        cnt = max (cnt,tmp);
        while (x < Constant.LEN - 1 && y < Constant.LEN - 1 && board[x + 1][y + 1] == role) {++x;++y;++tmp;}
        x = row;y = col;tmp = 1;
        while (x >= 1 && y < Constant.LEN - 1 && board[x - 1][y + 1] == role) {--x;++y;++tmp;}
        x = row;y = col;
        while (x < Constant.LEN - 1 && y >= 1 && board[x + 1][y - 1] == role) {++x;--y;++tmp;}
        cnt = max (cnt,tmp);
        return cnt;
    }

    private void TIME ()
    {
        Thread timerThread = new Thread (() ->
        {
            while (!br)
            {
                try
                {
                    Thread.sleep (1000);
                    if (!stop)
                    {
                        Platform.runLater (() ->
                        {
                            Side cur = Gomoku.getCurrentSide ();
                            if (cur == Side.BLACK) ++Black_ti;
                            else ++White_ti;
                            upd_time ();
                            check_time (cur);
                        });
                    }
                }
                catch (InterruptedException e) {e.printStackTrace ();}
            }
        });
        timerThread.setDaemon (true);timerThread.start ();
    }

    private void upd_time ()
    {
        String B_time = formatTime (tot - Black_ti);String W_time = formatTime (tot - White_ti);
        B_ti.setText ("Rest time : " + B_time);W_ti.setText ("Rest time : " + W_time);
    }
    private void check_time (Side cur)
    {
        if ((cur == Side.BLACK && Black_ti >= tot) || (cur == Side.WHITE && White_ti >= tot))
        {
            Platform.runLater (() ->
            {
                Alert alert = new Alert (Alert.AlertType.INFORMATION);
                alert.setTitle ("Gomoku Game");
                alert.setHeaderText (null);
                alert.setContentText ((cur == Side.BLACK ? "Black" : "White") + "Time out!");
                alert.showAndWait ();
                end ("Game Over due to timeout");
            });
        }
    }

    private String formatTime (int time) {return String.format ("%02d:%02d", time / 60,time % 60);}

    private void end (String s)
    {
        stop = !stop;
        Alert alert = new Alert (Alert.AlertType.CONFIRMATION);
        alert.setHeaderText ("Pause");alert.setTitle ("Gomoku Game");alert.setContentText (s);
        ButtonType buttonTypeRestart = new ButtonType ("New Game", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeContinue = new ButtonType ("Back", ButtonBar.ButtonData.NO);
        alert.getButtonTypes ().setAll (buttonTypeRestart, buttonTypeContinue);
        alert.showAndWait ().ifPresent (response ->
        {
            stop = !stop;
            if (response == buttonTypeRestart) {br = true;Start.showStartPage ();}
        });
    }

    public static void upd_st (int[][] board, Side currentPlayer)
    {
        undoStack.push (new GameState (board, currentPlayer));
        redoStack.clear ();
        upd_button ();
    }
    private void undo ()
    {
        if (undoStack.size () > 1)
        {
            undoStack.pop ();
            redoStack.push (new GameState (Gomoku.getChess (), Gomoku.getCurrentSide ()));
            GameState gameState = undoStack.peek ();
            loadGameState (gameState);
            if (Start.ty == 1) // 双人模式下撤销两次
            {
                if (undoStack.size () > 1)
                {
                    undoStack.pop ();
                    redoStack.push (new GameState (Gomoku.getChess (), Gomoku.getCurrentSide ()));
                    gameState = undoStack.peek ();

                }
            }
            loadGameState (gameState);
            upd_button ();
        }
    }

    private void redo ()
    {
        if (!redoStack.isEmpty ())
        {
            GameState gameState = redoStack.pop ();
            loadGameState (gameState);
            undoStack.push (new GameState (Gomoku.getChess (), Gomoku.getCurrentSide ()));
            if (Start.ty == 1)
            {
                if (!redoStack.isEmpty ())
                {
                    gameState = redoStack.pop ();
                    loadGameState (gameState);
                    undoStack.push (new GameState (Gomoku.getChess (), Gomoku.getCurrentSide ()));

                }
            }
            upd_button ();
        }
    }

    private void loadGameState (GameState state)
    {
        Gomoku.setChess (state.board_st);
        Gomoku.setCurrentSide (state.player);
        Board.draw_chess (Constant.sz);
    }
}