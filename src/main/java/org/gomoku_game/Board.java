package org.gomoku_game;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Board extends Pane
{
    private Canvas boardCanvas,chessCanvas;
    private GraphicsContext boardPencil;
    private static GraphicsContext chessPencil;
    private Gomoku gomoku;
    private void draw_cir (double x,double y)//绘制四个小圆点
    {
        double r = 3;
        boardPencil.fillOval (Constant.board_del + x - r, Constant.board_del + y - r, 2 * r, 2 * r);
    }
    public void draw_pane (double del)
    {
        boardPencil.clearRect (0, 0, Constant.width, Constant.height);
        boardPencil.setStroke (Color.BLACK);
        for (int i = 0;i < Constant.LEN - 1;++i)
        {
            for (int j = 0;j < Constant.LEN - 1;++j)
            {
                boardPencil.strokeRect (Constant.board_del + i * del, Constant.board_del + del * j, del, del);
                if (i == 3 && j == 3) draw_cir (i * del, del * j);
                if (i == 3 && j == 15) draw_cir (i * del, del * j);
                if (i == 15 && j == 3) draw_cir (i * del, del * j);
                if (i == 15 && j == 15) draw_cir (i * del, del * j);
                if (i == 9 && j == 9) draw_cir (i * del, del * j);
            }
        }
    }
    public static void draw_chess (double del)
    {
        chessPencil.clearRect (0, 0, Constant.width, Constant.height);//清空画布
        int chess[][] = Gomoku.getChess ();double r = Constant.chess_sz;
        for (int i = 0;i < Constant.LEN;++i)
        {
            for (int j = 0;j < Constant.LEN;++j)
            {
                double dx = Constant.board_del + i * del - del / 2,dy = Constant.board_del + j * del - del / 2;
                if (chess[i][j] == Side.BLACK.getState ())
                {
                    chessPencil.setFill (Color.BLACK);
                    chessPencil.fillOval (dx,dy,r,r);
                }
                else if (chess[i][j] == Side.WHITE.getState ())
                {
                    chessPencil.setFill (Color.WHITE);
                    chessPencil.fillOval (dx,dy,r,r);chessPencil.strokeOval (dx,dy,r,r);
                }
            }
        }
    }

    public Board (Gomoku gomoku)
    {
        this.gomoku = gomoku;
        boardCanvas = new Canvas (Constant.width, Constant.height);
        chessCanvas = new Canvas (Constant.width, Constant.height);
        boardPencil = boardCanvas.getGraphicsContext2D ();
        chessPencil = chessCanvas.getGraphicsContext2D ();
        draw_chess (Constant.sz);draw_pane (Constant.sz);
        getChildren().addAll (boardCanvas, chessCanvas);
    }
}