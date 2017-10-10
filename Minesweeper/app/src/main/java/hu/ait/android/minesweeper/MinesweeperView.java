package hu.ait.android.minesweeper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MinesweeperView extends View {

    private Paint paintBg;
    private Paint paintSquare;
    private Paint paintLine;
    private Paint paintFlag;

    private int[] numColors = {Color.CYAN, Color.GREEN, Color.MAGENTA, Color.BLUE,
        Color.RED, Color.YELLOW, Color.BLACK, Color.LTGRAY};

    private boolean flagging;

    public MinesweeperView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initPaint();

        flagging = false;
    }

    private void initPaint() {
        paintBg = new Paint();
        paintBg.setColor(Color.DKGRAY);
        paintBg.setStyle(Paint.Style.FILL);

        paintSquare = new Paint();
        paintSquare.setColor(Color.GRAY);
        paintSquare.setStyle(Paint.Style.FILL);

        paintLine = new Paint();
        paintLine.setColor(Color.BLACK);
        paintLine.setStrokeWidth(5);
        paintLine.setStyle(Paint.Style.STROKE);

        paintFlag = new Paint();
        paintFlag.setColor(Color.RED);
        paintFlag.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, getWidth(), getHeight(), paintBg);

        drawGameActivity(canvas);

        drawGameArea(canvas);

    }

    private void drawGameArea(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), paintLine);

        for(int i = 0; i < MinesweeperModel.NUM_ROWS; i++) {
            canvas.drawLine(0,
                    i * (getHeight() / MinesweeperModel.NUM_ROWS), getWidth(),
                    i * (getHeight() / MinesweeperModel.NUM_ROWS), paintLine);
        }

        for(int i = 0; i < MinesweeperModel.NUM_COLS; i++) {
            canvas.drawLine(i * (getWidth() / MinesweeperModel.NUM_COLS), 0,
                    i * (getWidth() / MinesweeperModel.NUM_COLS), getHeight(), paintLine);
        }
    }

    private void drawGameActivity(Canvas canvas) {
        int offsetX = getWidth()/MinesweeperModel.NUM_ROWS;
        int offsetY = getHeight()/MinesweeperModel.NUM_COLS;

        for (int i = 0; i < MinesweeperModel.NUM_ROWS; i++) {
            for (int j = 0; j < MinesweeperModel.NUM_COLS; j++) {
                switch(MinesweeperModel.getInstance().getFieldContent(i, j)) {
                    case MinesweeperModel.INVISIBLE:
                        break;
                    case MinesweeperModel.MINE:
                        drawMine(canvas, i, j);
                        break;
                    case MinesweeperModel.EMPTY:
                        canvas.drawRect(i * offsetX, j * offsetY,
                                (i + 1) * offsetX, (j + 1) * offsetY, paintSquare);
                        break;
                    case MinesweeperModel.FLAGGED:
                        drawFlag(canvas, i, j);
                        break;
                    default:
                        drawCount(canvas, i, j);
                        break;
                }
            }
        }
    }

    private void drawCount(Canvas canvas, int row, int col) {
        int offsetX = getWidth()/MinesweeperModel.NUM_ROWS;
        int offsetY = getHeight()/MinesweeperModel.NUM_COLS;
        int count = MinesweeperModel.getInstance().getFieldContent(row, col);

        canvas.drawRect(row * offsetX, col * offsetY,
                (row + 1) * offsetX, (col + 1) * offsetY, paintSquare);

        paintLine.setColor(numColors[count-1]);

        paintLine.setTextSize(offsetX / 2);

        canvas.drawText(Integer.toString(count),
                row * offsetX + offsetX / 4,
                col * offsetY + offsetY / 2,
                paintLine);

        paintLine.setColor(Color.BLACK);
    }

    private void drawMine(Canvas canvas, int row, int col) {
        int offsetX = getWidth()/MinesweeperModel.NUM_ROWS;
        int offsetY = getHeight()/MinesweeperModel.NUM_COLS;

        paintSquare.setColor(Color.RED);
        paintLine.setColor(Color.DKGRAY);

        canvas.drawRect(row * offsetX, col * offsetY,
                (row + 1) * offsetX, (col + 1) * offsetY, paintSquare);

        canvas.drawLine(row * offsetX + offsetX / 2, col * offsetY + offsetY / 6,
                row * offsetX + offsetX / 2, col * offsetY + 5 * offsetY / 6, paintLine);
        canvas.drawLine(row * offsetX + offsetX / 6, col * offsetY + offsetY / 2,
                row * offsetX + 5 * offsetX / 6, col * offsetY + offsetY / 2, paintLine);
        canvas.drawLine(row * offsetX + offsetX / 4, col * offsetY + offsetY / 4,
                row * offsetX + 3 * offsetX / 4, col * offsetY + 3 * offsetY / 4, paintLine);
        canvas.drawLine(row * offsetX + offsetX / 4, col * offsetY + 3 * offsetY / 4,
                row * offsetX + 3 * offsetX / 4, col * offsetY + offsetY / 4, paintLine);

        canvas.drawCircle(row * offsetX + offsetX / 2,
                col * offsetY + offsetY / 2,
                offsetX / 4, paintBg);

        paintSquare.setColor(Color.GRAY);
        paintLine.setColor(Color.BLACK);
    }

    private void drawFlag(Canvas canvas, int row, int col) {
        int offsetX = getWidth()/MinesweeperModel.NUM_ROWS;
        int offsetY = getHeight()/MinesweeperModel.NUM_COLS;

        canvas.drawLine(row * offsetX + 3 * offsetX / 4, col * offsetY + offsetY / 3,
                row * offsetX + 3 * offsetX / 4, col * offsetY + 5 * offsetY / 6, paintLine);

        paintFlag.setStrokeWidth(4);
        paintFlag.setStyle(Paint.Style.FILL_AND_STROKE);
        paintFlag.setAntiAlias(true);

        Point a = new Point(row * offsetX + 3 * offsetX / 4, col * offsetY + offsetY / 3);
        Point b = new Point(row * offsetX + offsetX / 4, col * offsetY + offsetY / 2);
        Point c = new Point(row * offsetX + 3 * offsetX / 4, col * offsetY + 2 * offsetY / 3);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(a.x, a.y);
        path.close();

        canvas.drawPath(path, paintFlag);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(MinesweeperModel.getInstance().getStatus() != MinesweeperModel.ONGOING)
            return false;

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = ((int) event.getX()) / (getWidth() /
                    MinesweeperModel.getInstance().NUM_ROWS);
            int y = ((int) event.getY()) / (getHeight() /
                    MinesweeperModel.getInstance().NUM_COLS);

            int flag = flagging ? MinesweeperModel.FLAGGED : MinesweeperModel.OPENED;

            if(MinesweeperModel.getInstance().setFieldContent(x, y, flag)) {
                ((MainActivity)getContext()).updateCounts(MinesweeperModel.getInstance().NUM_FLAGS);
                invalidate();
            }

            checkForStatusChange();

        }

        return super.onTouchEvent(event);
    }

    private void checkForStatusChange() {
        int status = MinesweeperModel.getInstance().getStatus();
        if(status == MinesweeperModel.WIN) {
            ((MainActivity)getContext()).displayResult(R.string.congrats);
            ((MainActivity)getContext()).changeFace("sunglasses");

            invalidate();
        } else if(status == MinesweeperModel.LOST) {
            if(flagging) {
                ((MainActivity)getContext()).displayResult(R.string.misplaced_flag);
            } else {
                ((MainActivity)getContext()).displayResult(R.string.hit_mine);
            }

            ((MainActivity)getContext()).changeFace("dead");
            invalidate();
        }
    }

    public void resetBoard(int size) {
        MinesweeperModel.getInstance().reset(size);
        flagging = false;
        ((MainActivity)getContext()).changeFace("smile");
        ((MainActivity)getContext()).updateCounts(MinesweeperModel.getInstance().NUM_FLAGS);
        invalidate();
    }

    public void toggleFlagging() {
        flagging = !flagging;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int d = w == 0 ? h : h == 0 ? w : w < h ? w : h;
        setMeasuredDimension(d, d);
    }
}
