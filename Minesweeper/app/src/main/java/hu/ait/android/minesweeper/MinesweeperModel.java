package hu.ait.android.minesweeper;

public class MinesweeperModel {

    private static MinesweeperModel minesweeperModel = null;

    private MinesweeperModel() {
        newGame(MinesweeperModel.EASY_SIZE);
    }

    public static MinesweeperModel getInstance() {
        if(minesweeperModel == null) {
            minesweeperModel = new MinesweeperModel();
        }

        return minesweeperModel;
    }

    public static int NUM_ROWS = 5;
    public static int NUM_COLS = 5;
    public static int NUM_MINES = 3;
    public static int NUM_FLAGS = 3;

    public static final int FLAGGED = -3;
    public static final int INVISIBLE = -2;
    public static final int MINE = -1;
    public static final int EMPTY = 0;
    public static final int CLOSED = 0;
    public static final int OPENED = 1;

    public static final int EASY_SIZE = 5;
    public static final int MED_SIZE = 8;
    public static final int HARD_SIZE = 12;

    private int[][] grid;
    private int[][] visible;

    private void newGame(int boardSize) {
        gameStatus = ONGOING;

        setConstants(boardSize);

        grid = new int[NUM_ROWS][NUM_COLS];
        visible = new int[NUM_ROWS][NUM_COLS];

        for(int r = 0; r < NUM_ROWS; r++)
            for(int c = 0; c < NUM_COLS; c++) {
                grid[r][c] = EMPTY;
                visible[r][c] = CLOSED;
            }

        int row, col;

        for(int i = 0; i < NUM_MINES; i++) {
            do {
                row = (int) (Math.random() * NUM_ROWS);
                col = (int) (Math.random() * NUM_COLS);
            } while(grid[row][col] != EMPTY);

            grid[row][col] = MINE;
        }

        for(int r = 0; r < NUM_ROWS; r++)
            for(int c = 0; c < NUM_COLS; c++)
                grid[r][c] = getMineCount(r, c);
    }

    private int getMineCount(int row, int col) {
        if(grid[row][col] == MINE)
            return MINE;

        int count = 0;

        for(int i = -1; i <= 1; i++)
            for(int j = -1; j <= 1; j++)
                if(inBounds(row+i, col+j) && !(i == 0 && j == 0))
                    if(grid[row+i][col+j] == MINE)
                        count++;

        return count;
    }

    private boolean inBounds(int r, int c) {
        return (r >= 0 && c >= 0 && r < NUM_ROWS && c < NUM_COLS);
    }

    public int getFieldContent(int r, int c) {
        if(visible[r][c] == OPENED)
            return grid[r][c];
        else if(visible[r][c] == FLAGGED)
            return FLAGGED;
        else
            return INVISIBLE;
    }

    public boolean setFieldContent(int r, int c, int flag) {
        
        if(visible[r][c] == OPENED)
            return false;

        visible[r][c] = flag;
        if(flag == FLAGGED) {
            if(grid[r][c] != MINE) {
                triggerGameOver();
                return true;
            }

            NUM_FLAGS--;

            if(NUM_FLAGS == 0) {
                triggerWin();
            }
        }

        if(flag != FLAGGED && grid[r][c] == MINE) {
            triggerGameOver();
            return true;
        }

        if(flag == OPENED && grid[r][c] == EMPTY)
            updateVisibilities(r,c);

        return true;
    }

    public static final int WIN = 1;
    public static final int ONGOING = 0;
    public static final int LOST = -1;
    private int gameStatus;

    private void triggerWin() {
        gameStatus = WIN;
    }

    private void triggerGameOver() {
        gameStatus = LOST;
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                if(grid[i][j] == MINE)
                    visible[i][j] = OPENED;
            }
        }
    }

    public int getStatus() { return gameStatus; }

    private void updateVisibilities(int r, int c) {
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                if (inBounds(r + i, c + j) && !(i == 0 && j == 0)) {
                    if (grid[r + i][c + j] != MINE && visible[r + i][c + j] == CLOSED) {
                        visible[r + i][c + j] = OPENED;
                        if (grid[r + i][c + j] == EMPTY)
                            updateVisibilities(r + i, c + j);
                    }
                }
    }

    public void reset(int size) {
        newGame(size);
    }

    public void setConstants(int size) {
        switch(size) {
            case MED_SIZE:
                NUM_ROWS = MED_SIZE;
                NUM_COLS = MED_SIZE;
                NUM_MINES = 5;
                NUM_FLAGS = 5;
                break;
            case HARD_SIZE:
                NUM_ROWS = HARD_SIZE;
                NUM_COLS = HARD_SIZE;
                NUM_MINES = 10;
                NUM_FLAGS = 10;
                break;
            case EASY_SIZE:
            default:
                NUM_ROWS = EASY_SIZE;
                NUM_COLS = EASY_SIZE;
                NUM_MINES = 3;
                NUM_FLAGS = 3;
        }
    }
}
