package com.example.runninggeoffrey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.runninggeoffrey.Game;
import java.util.Arrays;

public class HighScoreActivity extends AppCompatActivity {
    private String[] savedscores;
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
        int finalscore = highScores.getInt("first",0) - 1;
        first.setText("🏆" + finalscore + "🏆");

    }
}
