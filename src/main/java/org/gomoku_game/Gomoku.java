package org.gomoku_game;
import java.util.ArrayList;
import java.util.List;

public class Gomoku
{
    private static Side currentSide = Side.BLACK;
    public static int[][] chess = new int[Constant.LEN + 1][Constant.LEN + 1];
    public static Side getCurrentSide () {return currentSide;}
    public static void setChess (int[][] nw)
    {
        for (int i = 0; i < Constant.LEN; ++i)
            for (int j = 0; j < Constant.LEN; ++j) chess[i][j] = nw[i][j];
    }
    public static void setCurrentSide (Side nw) {currentSide = nw;}
    public static int[][] getChess () {return chess;}
    public void changeSide () {setCurrentSide (currentSide == Side.BLACK ? Side.WHITE : Side.BLACK);}

    public Gomoku ()
    {
        for (int i = 0; i < Constant.LEN; ++i)
            for (int j = 0; j < Constant.LEN; ++j) chess[i][j] = ' ';
        setCurrentSide (Side.BLACK);
    }

    public boolean play (int x,int y)
    {
        if (x < 0 || x >= Constant.LEN || y < 0 || y >= Constant.LEN) return false;
        if (chess[x][y] == ' ')
        {
            chess[x][y] = currentSide.getState ();
            System.out.printf ("%d %d %d\n",x,y,chess[x][y]);
            return true;
        }
        else return false;
    }
    public boolean full (int board[][])
    {
        for (int i = 0; i < Constant.LEN; ++i)
            for (int j = 0; j < Constant.LEN; ++j)
                if (board[i][j] == ' ') return false;
        return true;
    }
    public boolean judge_win (int board[][],int row,int col,Side color)
    {
        //检验从 (row,col) 开始 判断棋子是否连成五子
        int l = col,r = col;
        while (l >= 1 && board[row][l - 1] == color.getState ()) --l;
        while (r < Constant.LEN - 1 && board[row][r + 1] == color.getState ()) ++r;
        if (r - l + 1 >= 5) return true;
        l = row;r = row;
        while (l >= 1 && board[l - 1][col] == color.getState ()) --l;
        while (r < Constant.LEN - 1 && board[r + 1][col] == color.getState ()) ++r;
        if (r - l + 1 >= 5) return true;
        int x = row,y = col,cnt = 1;
        while (x >= 1 && y >= 1 && board[x - 1][y - 1] == color.getState ()) {--x;--y;++cnt;}
        x = row;y = col;
        while (x < Constant.LEN - 1 && y < Constant.LEN - 1 && board[x + 1][y + 1] == color.getState ()) {++x;++y;++cnt;}
        if (cnt >= 5) return true;
        x = row;y = col;cnt = 1;
        while (x >= 1 && y < Constant.LEN - 1 && board[x - 1][y + 1] == color.getState ()) {--x;++y;++cnt;}
        x = row;y = col;
        while (x < Constant.LEN - 1 && y >= 1 && board[x + 1][y - 1] == color.getState ()) {++x;--y;++cnt;}
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
                if (board[i][j] != ' ' && judge_win (board,i,j,board[i][j] == 1 ? Side.WHITE : Side.BLACK))
                {
                    rec[0] = 1000000;
                    return rec;
                }
        if (full (board) || dep == Constant.MAX_DEPTH)
        {
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
                rec[1] = dir.get (bestIndex)[0];rec[2] = dir.get (bestIndex)[1];
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
    private int evaluate (int[][] board,int role)
    {
        int score = 0;
        for (int i = 0;i < Constant.LEN;++i)
        {
            for (int j = 0;j < Constant.LEN;++j)
            {
                if (board[i][j] == role)
                {
                    if (j == 0 || board[i][j - 1] != role) score += eval_dir (board,i,j,0,1,role);
                    if (i == 0 || board[i - 1][j] != role) score += eval_dir (board,i,j,1,0,role);
                    if (i == 0 || j == 0 || board[i - 1][j - 1] != role)  score += eval_dir (board,i,j,1,1,role);
                    if (i == 0 || j == Constant.LEN - 1 || board[i - 1][j + 1] != role) score += eval_dir (board,i,j,1,-1,role);
                }
                else if (board[i][j] == (role ^ 1))
                {
                    int opp = 0;//对手的威胁
                    opp += opp_eval (board,i,j,0,1,role ^ 1);
                    opp += opp_eval (board,i,j,1,0,role ^ 1);
                    opp += opp_eval (board,i,j,1,1,role ^ 1);
                    opp += opp_eval (board,i,j,1,-1,role ^ 1);
                    if (opp >= 10000000) score -= 80000000; //活四/连五
                    else if (opp >= 1000000) score -= opp * 15;//冲四
                    else score -= opp * 4.8;
                }
            }
        }
        score += eval_gap (board,role);score -= eval_gap (board,role ^ 1) * 1.2;
        return score;
    }

    private int eval_gap (int[][] board, int role) //类似 BBXBB 这种
    {
        int score = 0;
        int[][] directions = {{0, 1},{1, 0},{1, 1}, {1, -1}, {0, -1}, {-1, 0}, {-1, -1}, {-1, 1}};
        for (int i = 0;i < Constant.LEN;++i)
        {
            for (int j = 0;j < Constant.LEN;++j)
            {
                if (board[i][j] != role) continue;
                for (int[] dir : directions) score += check_gap (board,i,j,dir[0],dir[1],role);
            }
        }
        return score;
    }

    // 单方向间隔检测
    private int check_gap (int[][] board,int x,int y,int dx,int dy,int role)
    {
        int score = 0,cnt = 1,gap_cnt = 0,xx = x + dx,yy = y + dy;
        while (check_bd (xx, yy))
        {
            if (board[xx][yy] == role) ++cnt;
            else if (board[xx][yy] == ' ' && gap_cnt < 1) ++gap_cnt;
            else break;
            xx += dx;yy += dy;
        }
        xx = x - dx;yy = y - dy;
        while (check_bd (xx, yy))
        {
            if (board[xx][yy] == role) ++cnt;
            else if (board[xx][yy] == ' ' && gap_cnt < 1) ++gap_cnt;
            else break;
            xx -= dx;yy -= dy;
        }
        switch (cnt)
        {
            case 4:
                if (gap_cnt == 1) score += 15000;//BBXBB型
                break;
            case 3:
                if (gap_cnt == 1) score += 8000;//BXXBB型
                break;
            case 2:
                if (gap_cnt == 1) score += 2000;//BXB型
                break;
        }
        if (cnt + gap_cnt >= 4) score += 20000;//特殊冲四
        return score;
    }

    private int eval_dir (int[][] board,int x,int y,int dx,int dy,int role)
    {
        int cnt = 1,xx = x + dx,yy = y + dy;
        boolean st1 = false,st2 = false;//两个方向
        while (check_bd (xx, yy) && board[xx][yy] == role) {++cnt;xx += dx;yy += dy;}
        st1 = !check_bd (xx, yy) || board[xx][yy] != ' ';
        xx = x - dx;yy = y - dy;
        while (check_bd (xx, yy) && board[xx][yy] == role) {++cnt;xx -= dx;yy -= dy;}
        st2 = !check_bd (xx, yy) || board[xx][yy] != ' ';
        switch (cnt)
        {
            case 5: return 50000000; //连五获胜
            case 4:
                if (!st2 && !st1) return 10000000;//活四
                if (!st2 || !st1) return 8000000;//冲四
                if (st1 || st2) return 20000;//有间隔的
                break;
            case 3:
                if (!st2 && !st1) return 100000;//活三
                if (!st2 || !st1) return 8000;//眠三
                break;
            case 2:
                if (!st2 && !st1) return 500;//活二
                if (!st2 || !st1) return 100;//眠二
                break;
            case 1:
                if (!st2 && !st1) return 30;
                break;
        }
        return 0;
    }
    private int opp_eval (int[][] board,int x,int y,int dx,int dy,int role)
    {
        int val = 0,cnt = 1,xx = x + dx,yy = y + dy;
        boolean st1 = false,st2 = false;
        while (check_bd (xx, yy) && board[xx][yy] == role) {++cnt;xx += dx;yy += dy;}
        st1 = !check_bd (xx, yy) || board[xx][yy] != ' ';
        xx = x - dx;yy = y - dy;
        while (check_bd (xx, yy) && board[xx][yy] == role) {++cnt;xx -= dx;yy -= dy;}
        st2 = !check_bd (xx, yy) || board[xx][yy] != ' ';
        switch (cnt)
        {
            case 4:
                if (!st1 || !st2) val = 50000000;//对手冲四
                break;
            case 3:
                if (!st1 && !st2) val = 10000000;//对手活三
                else if (!st1 || !st2) val = 800000;//对手眠三
                break;
            case 2:
                if (!st1 && !st2) val = 1000;//对手活二
                break;
            case 1 :
                if ((!st1 && !st2)) val = 100;
                break;
        }
        if (cnt == 3 && extra (board,x,y,dy,dx,role)) val <<= 1;//双活三
        return val;
    }

    private boolean extra (int[][] board,int x,int y,int dx,int dy,int role)
    {
        int xx = x + dx,yy = y + dy,cnt = 1;
        while (check_bd (xx, yy) && board[xx][yy] == role) {++cnt;xx += dx;yy += dy;}
        xx = x - dx;yy = y - dy;
        while (check_bd (xx, yy) && board[xx][yy] == role) {++cnt;xx -= dx;yy -= dy;}
        return cnt >= 3;
    }

    private boolean check_bd (int x, int y)
    {
        if (x >= Constant.LEN || x < 0) return false;
        if (y >= Constant.LEN || y < 0) return false;
        return true;
    }
}