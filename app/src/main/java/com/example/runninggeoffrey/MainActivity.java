package com.example.runninggeoffrey;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    MediaPlayer media;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        media = MediaPlayer.create(this, R.raw.song);
        play();
        Button start = findViewById(R.id.start);
        Button highScore = findViewById(R.id.high_score);
        highScore.setOnClickListener(v -> {
            startActivity(new Intent(this, HighScoreActivity.class));
        });
        start.setOnClickListener(v -> {
            startActivity(new Intent(this, Game.class));
        });
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
}
