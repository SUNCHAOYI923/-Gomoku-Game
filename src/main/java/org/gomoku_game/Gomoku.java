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
    private int evaluate (int[][] board, int role)
    {
        for (int i = 0;i < Constant.LEN;++i)
        {
            for (int j = 0;j < Constant.LEN;++j)
            {
                System.out.printf ("%d ",board[i][j]);
            }
            System.out.printf("\n");
        }
        int weights[] = {0, 200, 100, 1000, 8000, 9000000},dx[] = {0, 0, 1, -1, 1, 1, -1, -1},dy[] = {1, -1, 0, 0, 1, -1, 1, -1};
        int score = 0,centerX = Constant.LEN / 2,centerY = Constant.LEN / 2;
        for (int i = 0;i < Constant.LEN;++i)
        {
            for (int j = 0;j < Constant.LEN;++j)
            {
                if (board[i][j] == ' ') continue;
                int color = board[i][j];
                int centerBonus = 0,dxCenter = Math.abs(i - centerX),dyCenter = Math.abs(j - centerY);
                if (dxCenter <= 2 && dyCenter <= 2) centerBonus = 500;
                else if (dxCenter <= 3 && dyCenter <= 3) centerBonus = 200;
                else if (dxCenter <= 4 && dyCenter <= 4) centerBonus = 100;
                for (int dir = 0;dir < 8;++dir)
                {
                    int length = 1,x = i + dx[dir],y = j + dy[dir];
                    while (check_bd(x, y) && board[x][y] == color)
                    {
                        ++length;
                        x += dx[dir];y += dy[dir];
                    }
                    x = i - dx[dir];y = j - dy[dir];
                    while (check_bd(x, y) && board[x][y] == color)
                    {
                        ++length;
                        x -= dx[dir];y -= dy[dir];
                    }
                    boolean openEnds = false;
                    int xf = i + dx[dir],yf = j + dy[dir];
                    int xb = i - dx[dir],yb = j - dy[dir];
                    if (check_bd(xf, yf) && board[xf][yf] == ' ' && check_bd(xb, yb) && board[xb][yb] == ' ') openEnds = true;
                    if (length >= 5 || (length == 4 && openEnds)) return (color == role) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                    int multiplier = 1;
                    if (openEnds)

                    {
                        switch (length)
                        {
                            case 4: multiplier = 200; break; // 冲四
                            case 3: multiplier = 2000; break; // 活三
                            case 2: multiplier = 30; break; // 活二
                        }
                    }
                    else
                    {
                        switch (length)
                        {
                            case 4: multiplier = 30; break; // 眠四
                            case 3: multiplier = 300; break; // 眠三
                        }
                    }
                    if (color != role && (length == 3 || length == 4)) multiplier *= 10;
                    score += (color == role ? 1 : -1) * weights[length] * multiplier * (1 + centerBonus);
                }
            }
        } // 极低分，确保AI必须防守
        System.out.printf ("score:%d\n",score);
        System.out.printf("-------------------------------------------------------------------------\n");
        return score;
    }
    private boolean check_bd (int x,int y)
    {
        if (x >= Constant.LEN || x < 0) return false;
        if (y >= Constant.LEN || y < 0) return false;
        return true;
    }
    /*
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
            int len = Math.max (Math.abs(dl[0] - dl[1]) + 1,Math.abs (dr[0] - dr[1]) + 1);
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
     */
}