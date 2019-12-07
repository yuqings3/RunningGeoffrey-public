package com.example.runninggeoffrey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class HighScoreActivity extends AppCompatActivity {
    MediaPlayer media;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        media = MediaPlayer.create(this, R.raw.song);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        Button back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });
        play();
        showScores();
    }
    public void play() {
        media.setLooping(true);
        media.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        media.release();
    }
    public void showScores() {
        SharedPreferences highScores = getSharedPreferences("HIGH_SCORE", Context.MODE_PRIVATE);
        TextView first = findViewById(R.id.first);
        TextView second = findViewById(R.id.second);
        TextView third = findViewById(R.id.thrid);
        TextView forth = findViewById(R.id.forth);
        TextView fifth = findViewById(R.id.fifth);
        first.setText(String.valueOf(highScores.getInt("first",0)));
        second.setText(String.valueOf(highScores.getInt("second", 0)));
        third.setText(String.valueOf(highScores.getInt("third", 0)));
        forth.setText(String.valueOf(highScores.getInt("forth", 0)));
        fifth.setText(String.valueOf(highScores.getInt("fifth", 0)));
    }
}
