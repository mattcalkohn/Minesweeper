package hu.ait.android.minesweeper;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private TextView flagView;
    private TextView mineView;
    private ImageButton imgBtn;
    private ToggleButton flagBtn;
    private Spinner spinner;
    private MinesweeperView minesweeperView;
    private LinearLayout layoutContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layoutContent = findViewById(R.id.layoutContent);

        viewInit();
        buttonInit();
        spinnerInit();
    }

    private void spinnerInit() {
        spinner = findViewById(R.id.spinOpt);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (flagBtn.isChecked())
                    flagBtn.setChecked(false);
                resetWithSize(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // empty
            }

        });
    }

    private void resetWithSize(int position) {
        switch(position) {
            case 0:
                minesweeperView.resetBoard(MinesweeperModel.EASY_SIZE);
                break;
            case 1:
                minesweeperView.resetBoard(MinesweeperModel.MED_SIZE);
                break;
            case 2:
                minesweeperView.resetBoard(MinesweeperModel.HARD_SIZE);
                break;
            default:
        }
    }

    private void buttonInit() {
        imgBtn = findViewById(R.id.resetBtn);
        flagBtn = findViewById(R.id.flagBtn);

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flagBtn.isChecked())
                    flagBtn.setChecked(false);
                resetWithSize(spinner.getSelectedItemPosition());
            }
        });

        flagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minesweeperView.toggleFlagging();
            }
        });
    }

    private void viewInit() {
        mineView = findViewById(R.id.minesCount);
        flagView = findViewById(R.id.flagsCount);
        minesweeperView = findViewById(R.id.minesweeperView);
    }

    public void displayResult(int resultID) {
        if(resultID != R.string.empty) {
            Snackbar.make(layoutContent, getString(resultID),
                    Snackbar.LENGTH_LONG).setAction(getString(R.string.retry), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (flagBtn.isChecked())
                        flagBtn.setChecked(false);
                    resetWithSize(spinner.getSelectedItemPosition());
                }
            }).show();
        }
    }

    public void changeFace(String face) {
        switch(face) {
            case "smile":
                imgBtn.setImageResource(R.drawable.smile);
                break;
            case "sunglasses":
                imgBtn.setImageResource(R.drawable.shades);
                break;
            default:
                imgBtn.setImageResource(R.drawable.dead);
        }
    }

    public void updateCounts(int flagged) {
        if(flagged < 10)
            mineView.setText("00" + flagged);
        else
            mineView.setText("0" + flagged);
        flagView.setText("00" + (MinesweeperModel.NUM_MINES - flagged));
    }
}
