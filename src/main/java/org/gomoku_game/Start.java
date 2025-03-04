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
    @Override
    public void start (Stage nw)
    {
        nw.setTitle ("Gomoku Start Page");
        VBox root = new VBox (20);
        Label titleLabel = new Label ("Welcome to Gomoku Game");
        Button one = new Button ("Single Player Mode");
        Button two = new Button ("Two Player Mode");
        root.setPadding (new javafx.geometry.Insets (10));
        root.getChildren ().addAll (titleLabel,one,two);
        Scene scene = new Scene (root, 300, 200);
        nw.setScene (scene);nw.show ();
        one.setOnAction (event ->
        {
            try
            {
                nw.hide ();ty = 0;
                New_Game game = new New_Game ();
                try {game.start (new Stage ());}
                catch (Exception e) {e.printStackTrace();}
            }
            catch (Exception e) {e.printStackTrace();}
        });
        two.setOnAction (event ->
        {
            try
            {
                nw.hide ();ty = 1;
                New_Game game = new New_Game ();
                try {game.start (new Stage ());}
                catch (Exception e) {e.printStackTrace();}
            }
            catch (Exception e) {e.printStackTrace();}
        });
    }
    public static void main (String[] args) {launch (args);}
}