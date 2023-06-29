package com.example.xyzreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.PersistableBundle;

public class MainActivity extends AppCompatActivity {

    public static int mCurrentPosition;
    private static final String CURRENT_POSITION = "com.example.xyzreader.CURRENT_POSITION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt("CURRENT_POSITION");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(CURRENT_POSITION, mCurrentPosition);
    }
}