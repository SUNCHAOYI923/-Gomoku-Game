package org.gomoku_game;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class New_Game extends Application
{
    private Label B_ti,W_ti;
    private int Black_ti = 0,White_ti = 0,tot = Constant.MAX_TIME;
    private boolean stop = false;
    @Override
    public void start (Stage nw)
    {
        Gomoku gomoku = new Gomoku ();
        final Board board = new Board (gomoku);
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
        VBox rightPanel = inf();
        borderPane.setRight (rightPanel);
        Scene scene = new Scene (borderPane, Constant.width, Constant.height);
        nw.setScene (scene);nw.setTitle ("Gomoku Game");nw.centerOnScreen ();nw.show ();
        TIME();
    }

    private VBox inf ()
    {
        VBox container = new VBox (10);
        container.setPadding (new Insets (10));
        container.setStyle ("-fx-background-color: #ffffcc;");

        HBox controlPanel = new HBox (10);
        controlPanel.setPadding (new Insets(5));
        Button pauseButton = new Button ("Pause");
        pauseButton.setOnAction (event -> Platform.runLater(() -> end ("New Game")));
        controlPanel.getChildren ().add (pauseButton);
        container.getChildren ().add (controlPanel);

        GridPane blackInfoPanel = new GridPane ();
        blackInfoPanel.setVgap (5);
        blackInfoPanel.setPadding (new Insets (5));
        Label blackPlayerInfo = new Label ("Black");
        B_ti = new Label ("Rest time : " + formatTime (tot));
        blackInfoPanel.add (blackPlayerInfo, 0, 0);
        blackInfoPanel.add (B_ti, 0, 1);
        container.getChildren ().add (blackInfoPanel);

        GridPane whiteInfoPanel = new GridPane ();
        whiteInfoPanel.setVgap (5);
        whiteInfoPanel.setPadding (new Insets (5));
        Label whitePlayerInfo = new Label ("White");
        W_ti = new Label ("Rest time : " + formatTime (tot));
        whiteInfoPanel.add (whitePlayerInfo, 0, 0);
        whiteInfoPanel.add (W_ti, 0, 1);
        container.getChildren ().add (whiteInfoPanel);
        return container;
    }

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
                            check_time(cur);
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
        alert.showAndWait().ifPresent (response ->
        {
            if (response == buttonTypeRestart) Start.showStartPage();
            else stop = !stop;
        });
    }
}