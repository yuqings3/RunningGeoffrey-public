package com.example.runninggeoffrey;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import com.example.runninggeoffrey.Adapter.RoadAdapter;

public class Game extends AppCompatActivity {

    private RelativeLayout activityGame;
    private RecyclerView rvRoad;
    private ImageView ivNoteBoy;
    private AudioRecordDemo audioRecordDemo;
    private MediaRecorderDemo mediaRecorderDemo;
    private RoadAdapter roadAdapter;

    public String TAG = getClass().getSimpleName();

    private Context mContext;
    Timer T = new Timer();
    int count1 = 0;

    private ArrayList<Integer> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_game);
        activityGame = (RelativeLayout) findViewById(R.id.activity_game);
        rvRoad = (RecyclerView) findViewById(R.id.rv_road);
        ivNoteBoy = (ImageView) findViewById(R.id.iv_note_boy);

        audioRecordDemo = new AudioRecordDemo();
        mediaRecorderDemo = new MediaRecorderDemo();

        initData();
        initListener();

        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        TextView score = findViewById(R.id.score);
                        score.setText("Score=" + count1);
                        count1++;
                    }
                });
            }
        }, 60, 60);

    }

    private void initData() {
        data = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            data.add(i);
        }
        roadAdapter = new RoadAdapter(data, mContext);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
        rvRoad.setLayoutManager(layoutManager);
        rvRoad.setAdapter(roadAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        audioRecordDemo.getNoiseLevel();
    }

    private boolean scroll = true;

    private void initListener() {
        rvRoad.setOnTouchListener((View v, MotionEvent event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //some code....
                    break;
                case MotionEvent.ACTION_UP:
                    v.performClick();
                    break;
                default:
                    break;
            }
            return true;
        });

        audioRecordDemo.setVolumeListener((double volumeValue) -> {
            if (scroll) {
                rvRoad.scrollBy((int) rvRoad.getX() + 16, 0);
            }
            if (isShowMessage) {
                return;
            }
            ivNoteBoy.setTranslationY(0);
            float tranY = 0;
            if (volumeValue > 57) {
                if (!start) {
                    start();
                }
                tranY = (float) (volumeValue * volumeValue / (2 * 10));
                Log.i(TAG, "volumeChangeListener: tranY=" + tranY);

                ivNoteBoy.setTranslationY(-tranY);
            }
            int firstPosition = ((LinearLayoutManager) rvRoad.getLayoutManager()).findFirstVisibleItemPosition();
            int lastPosition = ((LinearLayoutManager) rvRoad.getLayoutManager()).findLastVisibleItemPosition();
            int count = lastPosition - firstPosition;
            for (int i = 0; i < count; i++) {
                View view = rvRoad.getChildAt(i);
                if (view.getTag().equals("W")) {
                    if (view.getX() < (ivNoteBoy.getX() + ivNoteBoy.getWidth() * 2 / 3) && view.getX() + view.getWidth() > (ivNoteBoy.getX() + ivNoteBoy.getWidth() * 1 / 3) && tranY == 0) {
                        scroll = false;
                        T.cancel();
                        SharedPreferences highScore = getSharedPreferences("HIGH_SCORE", Context.MODE_PRIVATE);
                        if (highScore.getInt("first", 0) <= count1) {
                            highScore.edit().putInt("first", count1).apply();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stop();
                                isShowMessage = true;
                                showDialog(mContext, "Oof!😱", "︎Geoff just falls down to a pit on his way to Foellinger", "-->high score", "-->restart", "-->home", true);
                            }
                        });
                    }
                }
            }
        });

    }

    private void stop() {
        rvRoad.removeCallbacks(goon);
        start = false;
    }

    private boolean start = false;
    private boolean isShowMessage = false;

    private Runnable goon = new Runnable() {
        @Override
        public void run() {
            rvRoad.scrollBy((int) rvRoad.getX(), (int) rvRoad.getY());
            start();
        }
    };

    private void start() {
        rvRoad.postDelayed(goon, 4);
        start = true;
    }

    public void showDialog(final Context ctx, final String titleName, final String message, final String highscoreButtonText, final String restartButtonText, final String cancelButtonText,
                           boolean isShowCancelButton) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ctx, R.style.AlertDialog);
            dialog.setTitle(titleName).setMessage(message);
            dialog.setPositiveButton(highscoreButtonText, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ctx, HighScoreActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ctx.startActivity(intent);
                    isShowMessage = false;
                    rvRoad.scrollToPosition(0);
                }
            });
            dialog.setNeutralButton(restartButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ctx, Game.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ctx.startActivity(intent);
                    isShowMessage = false;
                    rvRoad.scrollToPosition(0);
                }
            });
            if (isShowCancelButton) {
                dialog.setNegativeButton(cancelButtonText, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ctx, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ctx.startActivity(intent);
                        isShowMessage = false;
                        rvRoad.scrollToPosition(0);

                    }
                });
            }

            dialog.setCancelable(false);
            AlertDialog toShow = dialog.create();
            toShow.show();
        } catch (Exception e) {
            Log.e("DialogUtils", e.toString());
        }
    }
    /**public void store (int score) {
        SharedPreferences highScore = getSharedPreferences("HIGH_SCORE", Context.MODE_PRIVATE);

        /**int[] scores = new int[5];
        int[] newScores = new int [6];
        scores[0] = highScore.getInt("first", 0);
        scores[1] = highScore.getInt("second", 0);
        scores[2] = highScore.getInt("third", 0);
        scores[3] = highScore.getInt("forth", 0);
        scores[4] = highScore.getInt("fifth", 0);
        for (int i = 0; i < scores.length; i++) {
            newScores[i] = scores[i];
        }
        newScores[5] = score;
        Arrays.sort(newScores);
        for (int i = 0; i < scores.length; i++) {
            scores[i] = newScores[i];
        }
        SharedPreferences.Editor editor1 = highScore.edit();
        editor1.putInt("first", scores[4]);
        SharedPreferences.Editor editor2 = highScore.edit();
        editor2.putInt("second", scores[3]);
        SharedPreferences.Editor editor3 = highScore.edit();
        editor3.putInt("third", scores[2]);
        SharedPreferences.Editor editor4 = highScore.edit();
        editor4.putInt("forth", scores[1]);
        SharedPreferences.Editor editor5 = highScore.edit();
        editor5.putInt("fifth", scores[0]);
        editor1.commit();
        editor2.commit();
        editor3.commit();
        editor4.commit();
        editor5.commit();

    }**/
}
