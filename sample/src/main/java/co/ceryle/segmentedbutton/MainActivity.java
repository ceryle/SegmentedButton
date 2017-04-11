package co.ceryle.segmentedbutton;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private SegmentedButtonGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        group = (SegmentedButtonGroup) findViewById(R.id.segmentedButtonGroup);
        button = (Button) findViewById(R.id.button);

        updateButton(group.getPosition());

        group.setOnClickedButtonListener(new SegmentedButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(int position) {
                updateButton(position);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = group.getPosition();
                position = ++position % 3;
                updateButton(position);
                group.setPosition(position, true);
            }
        });


        group.setEnabled(false);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                group.setEnabled(true);
            }
        };
        handler.postDelayed(runnable, 5000);
    }

    private void updateButton(int position) {
        button.setText("Position: " + position);
    }
}