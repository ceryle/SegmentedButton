package com.seart.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    LinearLayout left_view, right_view, main_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_view = (LinearLayout) findViewById(R.id.main_view);
        left_view = (LinearLayout) findViewById(R.id.left_view);
        right_view = (LinearLayout) findViewById(R.id.right_view);



        main_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(event.getX() < textWidth){
                        AnimationCollapse.expand(left_view, 500, 0);
                        AnimationCollapse.expand(right_view, 500, (int)textWidth);
                    }
                    else if(event.getX() < textWidth*2){
                        AnimationCollapse.expand(left_view, 500, (int)textWidth);
                        AnimationCollapse.expand(right_view, 500, (int)textWidth*2);
                    }
                    else{
                        AnimationCollapse.expand(left_view, 500, (int)textWidth*2);
                        AnimationCollapse.expand(right_view, 500, (int)textWidth*3);
                    }
                }
                return true;
            }
        });

        final TextView text1 = (TextView) findViewById(R.id.text1);
        text1.post(new Runnable() {
            @Override
            public void run() {
                textWidth = text1.getWidth();

                AnimationCollapse.expand(left_view, 0, 0);
                AnimationCollapse.expand(right_view, 0, (int)textWidth);
            }
        });
    }

    float textWidth;
}
