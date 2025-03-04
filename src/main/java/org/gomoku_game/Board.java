package org.gomoku_game;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Board extends Pane
{
    public Canvas canvas;
    private Circle circle;
    public GraphicsContext pencil;
    private Gomoku gomoku;
    private void draw_cir (double x,double y)
    {
        double r = 3;
        circle = new Circle (Constant.board_del + x,Constant.board_del + y,r);
        getChildren ().add (circle);
    }
    public void draw_pane (double del)
    {
        canvas = new Canvas (Constant.width,Constant.height);
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
                if (chess[i][j] == Side.BLACK.getState ())
                {
                    pencil.setFill (Color.BLACK);
                    pencil.fillOval (dx,dy,r,r);
                }
                else if (chess[i][j] == Side.WHITE.getState ())
                {
                    pencil.setFill (Color.WHITE);
                    pencil.fillOval (dx,dy,r,r);pencil.strokeOval (dx,dy,r,r);
                }
            }
        }
    }
    public void drawPreviewChess (double mouseX, double mouseY)
    {
        double r = Constant.chess_sz;
        int row = (int) ((mouseX - Constant.board_del) / r);
        int col = (int) ((mouseY - Constant.board_del) / r);
        if (row >= 0 && row < Constant.LEN && col >= 0 && col < Constant.LEN)
        {
            double x = Constant.board_del + row * r - r / 2;
            double y = Constant.board_del + col * r - r / 2;
            Side currentSide = gomoku.getCurrentSide ();
            Color color = currentSide == Side.BLACK ? Color.BLACK : Color.WHITE;
            pencil.setFill (color.deriveColor (0, 1, 1, 0.5));
            pencil.fillOval (x, y, r, r);
        }
    }

    public Board (Gomoku gomoku)
    {
        this.gomoku = gomoku;
        draw_pane (Constant.sz);draw_chess (Constant.sz);
        getChildren ().add (canvas);
    }
}