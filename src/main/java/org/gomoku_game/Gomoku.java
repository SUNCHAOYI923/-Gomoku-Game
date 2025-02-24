package org.gomoku_game;

public class Gomoku {
    private static Side currentSide = Side.BLACK;
    public static int[][] chess = new int[Constant.LEN + 1][Constant.LEN + 1];

    public Side getCurrentSide () {return currentSide;}
    public void setCurrentSide (Side currentSide) {this.currentSide = currentSide;}
    public static int[][] getChess() {return chess;}
    public void changeSide () {setCurrentSide (currentSide == Side.BLACK ? Side.WHITE : Side.BLACK);}

    public Gomoku ()
    {
        for (int i = 0; i < Constant.LEN; ++i)
            for (int j = 0; j < Constant.LEN; ++j) chess[i][j] = ' ';
    }

    public void play (int x, int y)
    {
        if (chess[x][y] == ' ')
        {
            chess[x][y] = currentSide.getState();
            changeSide ();
        }
    }

    public boolean judge_win (int row,int col,Side color)
    {
        //检验从 (row,col) 开始 判断棋子是否连成五子
        int l = col,r = col;
        while (l >= 1 && chess[row][l - 1] == color.getState()) --l;
        while (r < Constant.LEN && chess[row][r + 1] == color.getState()) ++r;
        if (r - l + 1 >= 5) return true;
        l = row;r = row;
        while (l >= 1 && chess[l - 1][col] == color.getState()) --l;
        while (r < Constant.LEN && chess[r + 1][col] == color.getState()) ++r;
        if (r - l + 1 >= 5) return true;
        int x = row,y = col,cnt = 1;
        while (x >= 1 && y >= 1 && chess[x - 1][y - 1] == color.getState()) {--x;--y;++cnt;}
        x = row;y = col;
        while (x < Constant.LEN && y < Constant.LEN && chess[x + 1][y + 1] == color.getState()) {++x;++y;++cnt;}
        if (cnt >= 5) return true;
        x = row;y = col;cnt = 1;
        while (x >= 1 && y < Constant.LEN && chess[x - 1][y + 1] == color.getState()) {--x;++y;++cnt;}
        x = row;y = col;
        while (x < Constant.LEN && y >= 1 && chess[x + 1][y - 1] == color.getState()) {++x;--y;++cnt;}
        if (cnt >= 5) return true;
        return false;
    }
}