package co.ceryle.segmentedbutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SegmentedButtonGroup group = (SegmentedButtonGroup) findViewById(R.id.segmentedButtonGroup);

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = ++position % 3;
                button.setText("Position: " + position);
                group.setPosition(position, true);
            }
        });
    }
}
