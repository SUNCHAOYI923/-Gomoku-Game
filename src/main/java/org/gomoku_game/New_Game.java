package org.gomoku_game;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class New_Game extends Application
{
    @Override
    public void start (Stage nw)
    {
        Gomoku gomoku = new Gomoku (19,19,28.0);
        Board board = new Board (gomoku);
        Scene scene = new Scene (board,800,700);
        board.setOnMouseClicked (new PlayAction (gomoku, board));
        nw.setScene (scene);nw.setTitle ("Gomoku Game");nw.show ();
    }
}