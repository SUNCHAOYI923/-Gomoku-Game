package org.gomoku_game;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;

public class PlayAction implements EventHandler <MouseEvent>
{
    private Gomoku gomoku;
    private Board board;
    private int ty;

    public PlayAction (Gomoku gomoku,Board board,int ty)
    {
        this.board = board;this.gomoku = gomoku;this.ty = ty;
    }

    private void end (String s)
    {
        Alert alert = new Alert (Alert.AlertType.INFORMATION);
        alert.setTitle ("Gomuku Game");alert.setHeaderText ("Hint");
        String msg = s;
        alert.setContentText (msg);alert.showAndWait ();
    }
    @Override
    public void handle (MouseEvent event)
    {
        int len = Constant.sz,sx = 0,sy = 0;
        sx = (int)((event.getX () - Constant.board_del + len / 2) / len);
        sy = (int)((event.getY () - Constant.board_del + len / 2) / len);
        gomoku.play (sx,sy);board.draw_chess (len);
        //write (sx,sy,ty)
        if (gomoku.judge_win (sx,sy,gomoku.getCurrentSide () == Side.BLACK ? Side.WHITE : Side.BLACK))
        {
            end ((gomoku.getCurrentSide () == Side.BLACK ? Side.WHITE : Side.BLACK).getPlayer() + " wins！");
            return ;
        }
        if (gomoku.full ()) {end ("Draw!");return ;}
        if (ty == 0) return ;
        for (int i = 1;i < 1000000;++i) ;
        int res[] = gomoku.dfs (1,gomoku.getChess (),Integer.MIN_VALUE,Integer.MAX_VALUE,gomoku.getCurrentSide () == Side.BLACK ? 0 : 1);
        sx = res[1];sy = res[2];
        gomoku.play (sx,sy);board.draw_chess (len);
        if (gomoku.judge_win (sx,sy,gomoku.getCurrentSide () == Side.BLACK ? Side.WHITE : Side.BLACK))
        {
            end ((gomoku.getCurrentSide () == Side.BLACK ? Side.WHITE : Side.BLACK).getPlayer() + " wins！");
            return ;
        }
        if (gomoku.full ()) {end ("Draw!");return ;}
    }
}