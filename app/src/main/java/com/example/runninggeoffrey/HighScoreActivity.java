package com.example.runninggeoffrey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;

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
