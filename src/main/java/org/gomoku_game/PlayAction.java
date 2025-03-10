package org.gomoku_game;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayAction implements EventHandler <MouseEvent>
{
    private Gomoku gomoku;
    private Board board;
    private int ty;

    public PlayAction (Gomoku gomoku,Board board,int ty)
    {
        this.board = board;this.gomoku = gomoku;this.ty = ty;
    }

    public static void end (String s)
    {
        Alert alert = new Alert (Alert.AlertType.CONFIRMATION);
        alert.setTitle ("Gomoku Game");alert.setHeaderText ("Game Over!\n" + s);alert.setContentText ("Restart the game?");
        ButtonType buttonTypeRestart = new ButtonType ("Yes", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeExit = new ButtonType ("No", ButtonBar.ButtonData.NO);

        alert.getButtonTypes ().setAll (buttonTypeRestart, buttonTypeExit);

        alert.showAndWait ().ifPresent (response ->
        {
            if (response == buttonTypeRestart) Start.showStartPage ();
            else if (response == buttonTypeExit) System.exit (0);
        });
    }
    @Override
    public void handle (MouseEvent event)
    {
        int chess[][] = Gomoku.getChess ();
        int len = Constant.sz,sx = 0,sy = 0;
        sx = (int)((event.getX () - Constant.board_del + len / 2) / len);
        sy = (int)((event.getY () - Constant.board_del + len / 2) / len);
        boolean valid = gomoku.play (sx,sy);
        if (valid == false) return ;
        board.draw_chess (len);
        //write (sx,sy,ty)
        if (gomoku.judge_win (chess,sx,sy,gomoku.getCurrentSide () == Side.BLACK ? Side.WHITE : Side.BLACK))
        {
            end ((gomoku.getCurrentSide () == Side.BLACK ? Side.WHITE : Side.BLACK).getPlayer() + " wins！");
            return ;
        }
        if (gomoku.full (chess)) {end ("Draw!");return ;}
        if (ty == 0) return ;
        //通过并发机制让人机模式延后一秒执行
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool (1);
        scheduler.schedule (() ->
        {
            int res[] = gomoku.dfs (1, chess, Integer.MIN_VALUE, Integer.MAX_VALUE, gomoku.getCurrentSide () == Side.BLACK ? 0 : 1);
            int nw_sx = res[1], nw_sy = res[2];
            gomoku.play (nw_sx, nw_sy); board.draw_chess (len);
            if (gomoku.judge_win (chess, nw_sx, nw_sy, gomoku.getCurrentSide () == Side.BLACK ? Side.WHITE : Side.BLACK))
            {
                end ((gomoku.getCurrentSide () == Side.BLACK ? Side.WHITE : Side.BLACK).getPlayer () + " wins！");
                return;
            }
            if (gomoku.full (gomoku.chess)) { end ("Draw!"); return; }
            scheduler.shutdown ();
        }, 1, TimeUnit.SECONDS);
    }
}