package com.example.myapplication;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    View view1, view2, view3, middle1, middle2, middle3, top2;
    int position = 0, prevPosition = 0;

    int numberOfButton = 3;

    View[] middles = new View[numberOfButton];
    View[] tops = new View[numberOfButton - 2]; // -2 en sol ve sağ çıkılıyor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        middle1 = findViewById(R.id.middle1);
        middle2 = findViewById(R.id.middle2);
        middle3 = findViewById(R.id.middle3);
        top2 = findViewById(R.id.top2);

        middles[0] = middle1;
        middles[1] = middle2;
        middles[2] = middle3;
        tops[0] = top2;

        changeViewColor();
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevPosition = position;
                position = 0;
                changeViewColor();

                doAnimation();

            }
        });
        view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevPosition = position;
                position = 1;
                changeViewColor();


                doAnimation();

            }
        });
        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevPosition = position;
                position = 2;
                changeViewColor();

                doAnimation();
            }
        });


        middle1.setVisibility(View.GONE);
        middle2.setVisibility(View.GONE);
        middle3.setVisibility(View.GONE);

        top2.setVisibility(View.GONE);
    }

    private void doAnimation() {
        int positionChange = Math.abs(position - prevPosition);

        if (positionChange != 0) {

            if (prevPosition < position) { // Soldan sağa doğru

                if (positionChange > 1) {
                    for (int i = 0; i < position + 1; i++) {
                        middles[i].setVisibility(View.VISIBLE);
                        if (i > 0 && i < position)
                            tops[i - 1].setVisibility(View.VISIBLE);
                    }
                } else
                    for (int i = prevPosition; i < position + 1; i++) {

                        if (middles[i].getVisibility() == View.GONE)
                            middles[i].setVisibility(View.VISIBLE);
                        else
                            tops[i - 1].setVisibility(View.VISIBLE);
                    }
            } else { // Sağdan sola doğru

                if (positionChange > 1) {
                    for (int i = 0; i < prevPosition + 1; i++) {
                        middles[i].setVisibility(View.GONE);
                        if (i > 0 && i < prevPosition)
                            tops[i - 1].setVisibility(View.GONE);
                    }
                } else
                    for (int i = prevPosition; i > position; i--) {
                        if (i-2 >= 0 && tops[i - 2].getVisibility() == View.VISIBLE)
                            tops[i - 2].setVisibility(View.GONE);
                        middles[i].setVisibility(View.GONE);
                        if(i==1){
                            middles[0].setVisibility(View.GONE);
                        }
                    }
            }
        }


    }

    enum State {
        LEFT, RIGHT, NO_CHANGE
    }

    State state;


    private void changeViewColor() {
        view1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        view2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        view3.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        View view = null;
        switch (position) {
            case 0:
                view = view1;
                break;
            case 1:
                view = view2;
                break;
            case 2:
                view = view3;
                break;
        }
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
    }


}
