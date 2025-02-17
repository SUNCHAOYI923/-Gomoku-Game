package org.gomoku_game;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Board extends Pane
{
    private Canvas canvas;
    private Circle circle;
    private GraphicsContext pencil;
    private Gomoku gomoku;

    public Board (Gomoku gomoku)
    {
        this.gomoku = gomoku;
        draw_pane (Constant.sz);draw_chess(Constant.sz);
        getChildren ().add (canvas);
    }
    private void draw_cir (double x,double y)
    {
        double r = 3;
        circle = new Circle (Constant.board_del + x,Constant.board_del + y,r);
        getChildren ().add (circle);
    }
    private void draw_pane (double del)
    {
        canvas = new Canvas (Constant.height,Constant.width);
        pencil = canvas.getGraphicsContext2D ();
        pencil.clearRect (0,0,Constant.LEN,Constant.LEN);pencil.setStroke (Color.BLACK);
        for (int i = 0;i < Constant.LEN - 1;++i)
        {
            for (int j = 0;j < Constant.LEN - 1;++j)
            {
                pencil.strokeRect (Constant.board_del + i * del,Constant.board_del + del * j,del,del);
                if (i == 3 && j == 3) draw_cir (i * del, del * j);
                if (i == 3 && j == 15) draw_cir (i * del, del * j);
                if (i == 15 && j == 3) draw_cir (i * del, del * j);
                if (i == 15 && j == 15) draw_cir (i * del, del * j);
                if (i == 9 && j == 9) draw_cir (i * del, del * j);
            }
        }
    }
    public void draw_chess (double del)
    {
        int chess[][] = Gomoku.getChess ();double r = Constant.chess_sz;
        for (int i = 0;i < Constant.LEN;++i)
        {
            for (int j = 0;j < Constant.LEN;++j)
            {
                double dx = Constant.board_del + i * del - del / 2,dy = Constant.board_del + j * del - del / 2;
                if (chess[i][j] == Side.BLACK.getCode ())
                {
                    pencil.setFill (Color.BLACK);
                    pencil.fillOval (dx,dy,r,r);
                }
                else if (chess[i][j] == Side.WHITE.getCode ())
                {
                    pencil.setFill(Color.WHITE);
                    pencil.fillOval (dx,dy,r,r);pencil.strokeOval (dx,dy,r,r);
                }
            }
        }
    }
}