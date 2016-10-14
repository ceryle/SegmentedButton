package co.ceryle.segmentedbutton;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SegmentedButtonGroup sbg = (SegmentedButtonGroup) findViewById(R.id.segmentedButtonGroup);
        sbg.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
            @Override
            public void onClickedButtonPosition(int position) {
                Toast.makeText(MainActivity.this, "Clicked: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        sbg.setPosition(2, 0);
    }
}
