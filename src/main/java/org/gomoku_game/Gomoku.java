package org.gomoku_game;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.ArrayList;
import java.util.List;

public class Gomoku
{
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

    public void play (int x,int y)
    {
        if (chess[x][y] == ' ')
        {
            chess[x][y] = currentSide.getState ();
            System.out.printf ("%d %d %d\n",x,y,chess[x][y]);
            changeSide ();
        }
    }
    public boolean full ()
    {
        for (int i = 0; i < Constant.LEN; ++i)
            for (int j = 0; j < Constant.LEN; ++j)
                if (chess[i][j] == ' ') return false;
        return true;
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
    public int[] dfs (int dep,int board[][],int alpha,int beta,int role)//Black role = 0
    {
        int rec[] = new int [3];//score,x,y
        List<int[][]> ch = new ArrayList<int[][]>();
        List<int[]> dir = new ArrayList<int[]>();
        List<Integer> score = new ArrayList<Integer>();
        for (int i = 0;i < Constant.LEN;++i)
            for (int j = 0;j < Constant.LEN;++j)
                if (judge_win (i,j,Side.BLACK) || judge_win (i,j,Side.WHITE))
                {
                    rec[0] = evaluate (board,role ^ 1);
                    return rec;
                }
        if (full () || dep == Constant.MAX_DEPTH)
        {
            System.out.printf("%d %d %d %d\n",dep,alpha,beta,role);
            rec[0] = evaluate (board,role ^ 1);
            return rec;
        }
        for (int i = 0;i < Constant.LEN;++i)
            for (int j = 0;j < Constant.LEN;++j)
            {
                if (board[i][j] != ' ') continue;
                int[][] nw = new int [Constant.LEN + 1][];
                for (int k = 0; k < Constant.LEN; ++k) nw[k] = board[k].clone ();//需要浅拷贝
                nw[i][j] = role;
                dir.add (new int[]{i,j});ch.add (nw);
                nw = null;//释放内存
            }
        if (dep % 2 == 1)
        {
            for (int[][] childBoard : ch)
            {
                int res[] = dfs (dep + 1,childBoard,alpha,beta,role ^ 1).clone ();
                score.add (res[0]);
                alpha = Math.max (alpha,res[0]);
                if (alpha >= beta) break;
            }
            if (dep == 1)
            {
                int bestIndex = score.indexOf (alpha);
                rec[1] = dir.get(bestIndex)[0];rec[2] = dir.get(bestIndex)[1];
            }
            rec[0] = beta;
            return rec;
        }
        else
        {
            for (int[][] childBoard : ch)
            {
                int res[] = dfs (dep + 1,childBoard,alpha,beta,role ^ 1).clone ();
                score.add(res[0]);
                beta = Math.min (alpha,res[0]);
                if (alpha >= beta) break;
            }
            rec[0] = beta;
            return rec;
        }
    }
    private int evaluate (int[][] board,int role) {
        int sum = 0;
        for (int i = 0; i < Constant.LEN; ++i)
            for (int j = 0; j < Constant.LEN; ++j)
                if (board[i][j] != ' ') sum += solve (board, i, j, role);
        for (int i = 0; i < Constant.LEN; ++i)
        {
            for (int j = 0; j < Constant.LEN; ++j)
                System.out.printf("%d ", board[i][j]);
        System.out.printf("\n");
    }
        System.out.printf("sum = %d\n",sum);
        System.out.printf("----------------------------------------\n");
        return sum;
    }
    private boolean check_bd (int x,int y,int dx,int dy)
    {
        if (dx == 1 && x + 1 >= Constant.LEN) return false;
        if (dy == 1 && y + 1 >= Constant.LEN) return false;
        if (dx == -1 && x < 1) return false;
        if (dy == -1 && y < 1) return false;
        return true;
    }
    private int solve (int[][] board,int row,int col,int role)
    {
        int ty = board[row][col] == role ? 1 : -1,color = board[row][col];
        double max_value = 0;
        int dx[] = {0,0,1,-1,1,1,-1,-1},dy[] = {1,-1,0,0,1,-1,1,-1};
        for (int i = 0;i < 4;++i)
        {
            int x = row,y = col,o = 2 * i;
            while (check_bd (x,y,dx[o],dy[o]) && board[x + dx[o]][y + dy[o]] == color) {x += dx[o];y += dy[o];}
            int dl[] = new int [2],dr[] = new int [2];
            dl[0] = x;dr[0] = y;
            x = row;y = col;
            o ^= 1;
            while (check_bd (x,y,dx[o],dy[o]) && board[x + dx[o]][y + dy[o]] == color) {x += dx[o];y += dy[o];}
            dl[1] = x;dr[1] = y;
            int len = Math.max (Math.abs(dl[0] - dl[1]),Math.abs (dr[0] - dr[1]));
            System.out.printf("%d\n",len);
            if (len >= 5) return 10000000 * ty;
            for (int j = 4;j >= 1;--j)
            {
                int leftside = -1,rightside = -1;
                o = i * 2;
                while (check_bd (x,y,dx[o],dy[o]) && board[x + dx[o]][y + dy[o]] == ' ') {x += dx[o];y += dy[o];rightside = 0;}
                o ^= 1;
                while (check_bd (x,y,dx[o],dy[o]) && board[x + dx[o]][y + dy[o]] == ' ') {x += dx[o];y += dy[o];leftside = 0;}
                if (leftside != -1)
                {
                    o = i * 2;
                    while (check_bd (x,y,dx[o],dy[o]) && board[x + dx[o]][y + dy[o]] == color) {x += dx[o];y += dy[o];++rightside;}
                }
                if (rightside != -1)
                {
                    o = i * 2 + 1;
                    while (check_bd (x,y,dx[o],dy[o]) && board[x + dx[o]][y + dy[o]] == color) {x += dx[o];y += dy[o];++leftside;}
                }
                max_value = Math.max(max_value,Math.max(0.0,8000 * Math.min (1.0,1.0 * leftside / (5 - j))) +  Math.max(0.0,8000 * Math.min (1.0,1.0 * rightside / (5 - j))));
            }
        }
        return (int)max_value * ty;
    }
}