package org.gomoku_game;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class Board extends Pane
{
    public Canvas canvas;
    public GraphicsContext pencil;
    public Gomoku gomoku;
    private static double height,width;

    public Board (Gomoku gomoku)
    {
        this.gomoku = gomoku;
        height = gomoku.getHeight ();
        width = gomoku.getWidth ();
        double cell= gomoku.getSz ();
        drawPane (cell);drawChess (cell);
        getChildren ().add (canvas);
    }

    public void drawPane(double cell)
    {
        canvas = new Canvas (800,700);
        pencil = canvas.getGraphicsContext2D ();
        pencil.clearRect (0,0,width,height);pencil.setStroke (Color.BLACK);
        for (int i = 0; i< width - 1;++i)
            for (int j = 0; j< height - 1;++j)
                pencil.strokeRect (100 + i * cell, 100 + cell * j, cell, cell);
    }

    /*渲染棋子*/
    public void drawChess(double cell){

        /*获取棋子的位置*/
        int[][] chess= gomoku.getChess();

        for(int i = 0; i< height; i++)
            for(int j = 0; j< width; j++){

                if(chess[i][j]== Side.BLACK.getCode()){
                    pencil.setFill(Color.BLACK);
                    pencil.fillOval(100+i*cell-cell/2,100+j*cell-cell/2,cell,cell);//画棋子
                }
                else if(chess[i][j]==Side.WHITE.getCode()){
                    pencil.setFill(Color.WHITE);

                    pencil.fillOval(100+i*cell-cell/2,100+j*cell-cell/2,cell,cell);
                    pencil.strokeOval(100+i*cell-cell/2,100+j*cell-cell/2,cell,cell);
                }
            }
    }
}

