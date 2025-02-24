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
        Scene scene = new Scene (board,Constant.width,Constant.height);
        board.setOnMouseClicked (new PlayAction (gomoku,board));
        board.setOnMouseMoved (event ->
        {
            double mouseX = event.getX (),mouseY = event.getY ();
            board.pencil.clearRect (0,0,board.canvas.getWidth (),board.canvas.getHeight ());
            board.draw_pane (Constant.sz);board.draw_chess (Constant.sz);
            board.drawPreviewChess(mouseX, mouseY, Constant.sz);
        });
        nw.setScene (scene);nw.setTitle ("Gomoku Game");nw.show ();
    }
}