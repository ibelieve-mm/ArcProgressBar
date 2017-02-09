package com.cme.mm.arcprogressbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private ArcProgressBar arcProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arcProgressBar = (ArcProgressBar) findViewById(R.id.percent_view2);

        arcProgressBar.setMaxProgress(8000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        arcProgressBar.setCurrentSweepAngle(3200);
    }
}