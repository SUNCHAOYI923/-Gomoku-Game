package org.gomoku_game;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class New_Game extends Application
{
    @Override
    public void start (Stage nw)
    {
        Gomoku gomoku = new Gomoku ();
        Board board = new Board (gomoku);
        Scene scene = new Scene (board,Constant.height,Constant.width);
        board.setOnMouseClicked (new PlayAction (gomoku,board));
        nw.setScene (scene);nw.setTitle ("Gomoku Game");nw.show ();
    }
}