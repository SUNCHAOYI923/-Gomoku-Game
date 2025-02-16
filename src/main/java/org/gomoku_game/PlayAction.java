package org.gomoku_game;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;

import java.io.*;

/**
 * 时间处理类，可以使用lamda表达式替代
 */
public class PlayAction implements EventHandler<MouseEvent> {

    /*fiveChess表示五子棋游戏模型*/

    private Gomoku gomoku;

    /*chessPane表示五子棋显示面板*/

    private Board board;



    public PlayAction(Gomoku gomoku, Board board){
        this.board = board;

        this.gomoku = gomoku;

    }

    @Override
    public void handle(MouseEvent event){

        //获取棋盘的小方格的大小
        double cell = gomoku.getSz();


        //获取鼠标点击坐标
        double x = event.getX();
        double y = event.getY();

        /*映射到数组中的坐标*/
        int i = (int)((x-100+cell/2)/cell);
        int j = (int)((y-100+cell/2)/cell);


        /*记录下落子位置*/


        /*改变棋盘状态*/
        gomoku.play(i,j);

        /*重新渲染棋子*/
        board.drawChess(cell);

        /*判断是否结束*/
        if(!gomoku.judgeGame(i,j, gomoku.getCurrentSide()== Side.BLACK?Side.WHITE:Side.BLACK)){

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("五子棋游戏");
            alert.setHeaderText("提示信息");

            String msg = (gomoku.getCurrentSide()==Side.BLACK?Side.WHITE:Side.BLACK).getDesc()+"取得胜利！";
            alert.setContentText(msg);



            alert.showAndWait();
        }

    }

}
