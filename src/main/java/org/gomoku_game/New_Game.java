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

public class New_Game extends Application
{
    public static int Black_ti = 0,White_ti = 0;
    private Label B_ti,W_ti;
    private int tot = Constant.MAX_TIME;
    private boolean stop = false;
    private static Stack <GameState> undoStack = new Stack <> ();
    private static Stack <GameState> redoStack = new Stack <> ();
    private static Button undoButton,redoButton,saveButton;

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
        Gomoku gomoku = new Gomoku ();
        final Board board = new Board (gomoku);
        if (Start.org != null) // 如果存在游戏状态，则加载游戏状态
        {
            Gomoku.setChess (Start.org.board_st);
            Gomoku.setCurrentSide (Start.org.player);
            board.draw_chess (Constant.sz);
            Start.org = null;
        }
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
        BorderPane borderPane = new BorderPane ();
        borderPane.setCenter (board);
        VBox rightPanel = inf ();
        borderPane.setRight (rightPanel);
        Scene scene = new Scene (borderPane, Constant.width, Constant.height);
        nw.setScene (scene);nw.setTitle ("Gomoku Game");nw.centerOnScreen ();nw.show ();
        TIME ();upd_button ();
    }

    private VBox inf ()
    {
        VBox container = new VBox (10);
        container.setPadding (new Insets (10));
        container.setStyle ("-fx-background-color: #ffffcc;");
        container.setPrefWidth (250);
        HBox controlPanel = new HBox (10);
        controlPanel.setPadding (new Insets (5));
        saveButton = new Button ("Save");
        saveButton.setOnAction (event -> File.save_game ());
        Button pauseButton = new Button ("Pause");
        pauseButton.setOnAction (event -> Platform.runLater (() -> end ("Pause")));
        undoButton = new Button ("Undo");
        undoButton.setOnAction (event -> {undo ();upd_button ();});//禁用/启用按钮的切换
        redoButton = new Button ("Redo");
        redoButton.setOnAction (event -> {redo ();upd_button ();});
        controlPanel.getChildren ().addAll (saveButton,pauseButton,undoButton,redoButton);
        container.getChildren ().add (controlPanel);

        GridPane blackInfoPanel = new GridPane ();
        blackInfoPanel.setVgap (5);
        blackInfoPanel.setPadding (new Insets (5));
        Label blackPlayerInfo = new Label ("Black");
        B_ti = new Label ("Rest time : " + formatTime (tot - Black_ti));
        blackInfoPanel.add (blackPlayerInfo, 0, 0);
        blackInfoPanel.add (B_ti, 0, 1);
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
        whiteInfoPanel.add (whitePlayerInfo, 0, 0);
        whiteInfoPanel.add (W_ti, 0, 1);
        container.getChildren ().add (whiteInfoPanel);
        return container;
    }
    public static void upd_button () {Platform.runLater (() ->
    {
        undoButton.setDisable (undoStack.isEmpty ());
        redoButton.setDisable (redoStack.isEmpty ());
        saveButton.setDisable (PlayAction.AI_turn);
    });}

    private void TIME ()
    {
        Thread timerThread = new Thread (() ->
        {
            while (true)
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
            if (response == buttonTypeRestart) Start.showStartPage();
            else stop = !stop;
        });
    }

    public static void upd_st (int[][] board, Side currentPlayer)
    {
        undoStack.push (new GameState (board, currentPlayer));redoStack.clear ();
        upd_button ();
    }
    private void undo ()
    {
        if (!undoStack.isEmpty ())
        {
            GameState gameState = undoStack.pop ();
            redoStack.push (new GameState (Gomoku.getChess (), Gomoku.getCurrentSide ()));
            loadGameState (gameState);
            if (Start.ty == 1) // 双人模式下撤销两次
            {
                if (!undoStack.isEmpty())
                {
                    gameState = undoStack.pop ();
                    redoStack.push (new GameState (Gomoku.getChess (), Gomoku.getCurrentSide ()));
                    loadGameState (gameState);
                }
            }
            upd_button ();
        }
    }

    private void redo ()
    {
        if (!redoStack.isEmpty ())
        {
            GameState gameState = redoStack.pop ();
            undoStack.push (new GameState (Gomoku.getChess (), Gomoku.getCurrentSide ()));
            loadGameState (gameState);
            if (Start.ty == 1)
            {
                if (!redoStack.isEmpty ())
                {
                    gameState = redoStack.pop ();
                    undoStack.push (new GameState (Gomoku.getChess (), Gomoku.getCurrentSide ()));
                    loadGameState (gameState);
                }
            }
            upd_button ();
        }
    }

    private void loadGameState (GameState state)
    {
        Gomoku.setChess (state.board_st);Gomoku.setCurrentSide (state.player);
        Board.draw_chess (Constant.sz);
    }
}