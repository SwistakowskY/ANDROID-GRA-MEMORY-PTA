package com.lambiengcode.memorygame;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.lambiengcode.memorygame.ui.Adapters.TileAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public static int time = 3;
    GridView gridViewShow, gridViewResult;
    TextView tvTime, tvTiles, tvScores;
    TileAdapter adapterResult, adapterShow;
    List<Boolean> results, shows;
    int tiles = 3, wins = 0, loses = 0, mScore = 0;
    public static double widthScreen = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }


        Init();

        gridViewShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(time == 0) {
                    if (results.get(position)) {
                        shows.set(position, true);
                        adapterShow.notifyDataSetChanged();
                        if(results.equals(shows)) {
                            Toast.makeText(MainActivity.this, "Swietnie! Nastepny poziom.",
                                    Toast.LENGTH_LONG).show();
                            wins++;
                            loses = 0;

                            mScore++;


                            if(wins == 3 && tiles < 12) {
                                tiles++;
                                wins = 0;
                            }


                            RestartGame();
                        }
                    }else {
                        Toast.makeText(MainActivity.this, "Zla odpowiedz, sprobuj jeszcze raz!",
                                Toast.LENGTH_LONG).show();
                        loses++;
                        wins = 0;
                        if(mScore != 0) mScore--;
                        if (loses == 3 && tiles > 3) {
                            tiles--;
                            loses = 0;
                        }


                        RestartGame();
                    }
                }
            }
        });
    }

    private void Init() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthScreen = size.x;
        gridViewShow = findViewById(R.id.myGridView);
        gridViewResult = findViewById(R.id.myGridViewResult);
        tvTime = findViewById(R.id.mTime);
        tvTiles = findViewById(R.id.mLevel);
        tvScores = findViewById(R.id.mScore);
        results = new ArrayList<>();
        shows = new ArrayList<>();
        CreateRandomList();
        ChangedTextView();
        adapterResult = new TileAdapter(this, results);
        adapterShow = new TileAdapter(this, shows);
        gridViewShow.setAdapter(adapterShow);
        gridViewResult.setAdapter(adapterResult);
        StartTimer();
    }

    private void RestartGame() {
        try {
            ChangedTextView();
            CreateRandomList();
            gridViewResult.setVisibility(View.VISIBLE);
            adapterShow.notifyDataSetChanged();
            adapterResult.notifyDataSetChanged();
            StartTimer();
        } catch (Exception e) {
            Log.d("", e.toString());
        }
    }

    private void CreateRandomList() {
        Random random = new Random();
        int bound = 36;
        results.clear();
        shows.clear();
        if (tiles >= 10) {
            results.addAll(Collections.nCopies(100, false));
            shows.addAll(results);
            gridViewShow.setNumColumns(10);
            gridViewResult.setNumColumns(10);
            bound = 100;
        }else if (tiles >= 5) {
            results.addAll(Collections.nCopies(64, false));
            shows.addAll(results);
            gridViewShow.setNumColumns(8);
            gridViewResult.setNumColumns(8);
            bound = 64;
        } else {
            results.addAll(Collections.nCopies(36, false));
            shows.addAll(results);
            gridViewShow.setNumColumns(6);
            gridViewResult.setNumColumns(6);
            bound = 36;
        }
        for (int i = 0; i < tiles; i++) {
            int ranNum = random.nextInt(bound);
            while (results.get(ranNum)) {
                ranNum = random.nextInt(bound);
            }
            results.set(ranNum, true);
        }
    }

    private void StartTimer() {
        Timer timer = new Timer();
        time = 3;
        tvTime.setVisibility(View.VISIBLE);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (time == 0) {
                    timer.cancel();
                    timer.purge();
                    tvTime.setVisibility(View.INVISIBLE);
                    gridViewResult.setVisibility(View.INVISIBLE);
                } else {
                    time--;
                    tvTime.setText(String.valueOf(time));
                }
            }
        }, 1000, 1000);
    }

    private void ChangedTextView() {
        tvTiles.setText("Kwadraty: " + tiles);
        tvScores.setText("Wynik: " + mScore);
    }
}