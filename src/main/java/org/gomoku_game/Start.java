package org.gomoku_game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Start extends Application
{
    public static int ty;
    private static Stage mainStage;

    @Override
    public void start (Stage stage) {mainStage = stage;showStartPage();}

    public static void showStartPage ()
    {
        Stage stage = mainStage; //使用保存的主舞台引用
        stage.setTitle ("Gomoku Game");
        VBox root = new VBox (20);
        Label titleLabel = new Label ("Welcome to Gomoku Game");
        Button one = new Button ("Single Player Mode");
        Button two = new Button ("Two Player Mode");
        root.setPadding (new javafx.geometry.Insets (10));
        root.getChildren ().addAll (titleLabel, one, two);
        Scene scene = new Scene (root, 300, 200);
        stage.setScene (scene);stage.centerOnScreen ();stage.show ();
        one.setOnAction (event -> {ty = 0;launchGame (stage);});
        two.setOnAction (event -> {ty = 1;launchGame (stage);});
    }

    private static void launchGame (Stage stage)
    {
        New_Game game = new New_Game ();
        game.start (stage);
    }

    public static void main (String[] args) {launch (args);}
}