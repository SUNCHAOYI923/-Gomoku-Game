package org.gomoku_game;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;

public class PlayAction implements EventHandler <MouseEvent>
{
    private Gomoku gomoku;
    private Board board;

    public PlayAction (Gomoku gomoku, Board board) {this.board = board;this.gomoku = gomoku;}

    @Override
    public void handle (MouseEvent event)
    {
        int len = Constant.sz;
        double x = event.getX(),y = event.getY();
        int i = (int)((x - Constant.board_del + len / 2) / len),j = (int)((y - Constant.board_del + len / 2) / len);
        gomoku.play (i,j);board.draw_chess (len);
        if (gomoku.judge_win (i,j,gomoku.getCurrentSide () == Side.BLACK ? Side.WHITE : Side.BLACK))
        {
            Alert alert = new Alert (Alert.AlertType.INFORMATION);
            alert.setTitle ("Gomuku Game");alert.setHeaderText ("Hint");
            String msg = (gomoku.getCurrentSide () == Side.BLACK ? Side.WHITE : Side.BLACK).getStr () + " wins！";
            alert.setContentText (msg);alert.showAndWait ();
        }
    }
}