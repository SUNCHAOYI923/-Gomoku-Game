package org.gomoku_game;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayAction implements EventHandler<MouseEvent>
{
    private Gomoku gomoku;
    private Board board;
    private int ty;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool (1);
    public static volatile boolean AI_turn = false; //标记此时是否为 AI 在下棋

    public PlayAction (Gomoku gomoku, Board board, int ty) {this.board = board;this.gomoku = gomoku;this.ty = ty;}

    public static void end (String s)
    {
        Platform.runLater (() ->
        { // 确保在JavaFX应用线程上执行
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
        });
    }

    @Override
    public void handle (MouseEvent event)
    {
        New_Game.upd_st (Gomoku.getChess (),gomoku.getCurrentSide ());
        if (AI_turn) return; // 如果AI正在下棋，则忽略玩家操作
        int chess[][] = Gomoku.getChess ();
        int len = Constant.sz,sx = 0,sy = 0;
        sx = (int)((event.getX () - Constant.board_del + len / 2) / len);
        sy = (int)((event.getY () - Constant.board_del + len / 2) / len);
        boolean valid = gomoku.play (sx,sy);
        if (valid == false) return ;
        board.draw_chess (len);
        //write (sx,sy,ty)
        if (gomoku.judge_win (chess,sx,sy,gomoku.getCurrentSide ()))
        {
            end ((gomoku.getCurrentSide ()).getPlayer() + " wins！");
            return ;
        }
        gomoku.changeSide ();New_Game.upd_button ();
        if (gomoku.full (chess)) {end ("Draw!");return ;}
        if (ty != 0 && gomoku.getCurrentSide() != Side.BLACK) AI_play();
    }

    private void ch_end (int sx,int sy)
    {
        if (gomoku.judge_win (Gomoku.getChess (), sx, sy, gomoku.getCurrentSide ()))
        {
            end (gomoku.getCurrentSide().getPlayer() + " wins！");
            return;
        }
        if (gomoku.full (gomoku.chess))
        {
            end ("Draw!");
            return;
        }
        gomoku.changeSide ();
    }

    private void AI_play ()
    {
        New_Game.upd_st (Gomoku.getChess (),gomoku.getCurrentSide ());
        AI_turn = true;
        scheduler.schedule (() ->
        {
            int res[] = gomoku.dfs (1, Gomoku.getChess (), Integer.MIN_VALUE, Integer.MAX_VALUE, gomoku.getCurrentSide () == Side.BLACK ? 0 : 1);
            final int nw_sx = res[1], nw_sy = res[2];
            Platform.runLater (() -> // 确保在JavaFX应用线程上执行
            {
                gomoku.play (nw_sx, nw_sy);
                board.draw_chess (Constant.sz);
                ch_end (nw_sx, nw_sy);
                AI_turn = false;New_Game.upd_button ();
            });
        }, 1, TimeUnit.SECONDS);
    }
}