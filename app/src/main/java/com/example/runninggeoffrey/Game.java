package com.example.runninggeoffrey;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;

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
            if (isShowMessage) {
                return;
            }
            if (volumeValue > 60) {
                if (!start) {
                    start();
                }
                float test = (float) (Math.sqrt(volumeValue - 30) * 10);
                Log.i(TAG, "volumeChangeListener: test=" + test);
                float tranY = test + (float) volumeValue;
                tranY = (float) (volumeValue * volumeValue / (2 * 10));
                Log.i(TAG, "volumeChangeListener: tranY=" + tranY);

                ivNoteBoy.setTranslationY(-tranY);
            } else {
                if (!start) {
                    return;
                }
                stop();
                ivNoteBoy.setTranslationY(0);
                if (rvRoad.getLayoutManager() instanceof LinearLayoutManager) {
                    int firstPosition = ((LinearLayoutManager) rvRoad.getLayoutManager()).findFirstVisibleItemPosition();
                    int lastPosition = ((LinearLayoutManager) rvRoad.getLayoutManager()).findLastVisibleItemPosition();
                    int count = lastPosition - firstPosition;
                    for (int i = 0; i < count; i++) {
                        View view = rvRoad.getChildAt(i);
                        if (view.getTag().equals("W")) {
                            if (view.getX() < (ivNoteBoy.getX() + ivNoteBoy.getWidth() * 2 / 3) && view.getX() + view.getWidth() > (ivNoteBoy.getX() + ivNoteBoy.getWidth() * 1 / 3)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        stop();
                                        isShowMessage = true;
                                        showDialog(mContext, "Oof!", "Geoff just falls down to a pit on his way to Foellinger", "high score", "restart",  "home", true);
                                    }
                                });
                            }
                        }
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
            rvRoad.scrollBy((int) rvRoad.getX() + 3, (int) rvRoad.getY());
            start();
        }
    };

    private void start() {
        rvRoad.postDelayed(goon, 2);
        start = true;
    }

    public void showDialog(final Context ctx, final String titleName, final String message, final String highscoreButtonText, final String restartButtonText, final String cancleButtonText,
                                  boolean isShowCancelButton) {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
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
                dialog.setNegativeButton(cancleButtonText, new DialogInterface.OnClickListener() {

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
}
