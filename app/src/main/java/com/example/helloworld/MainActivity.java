package com.example.helloworld;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FrameLayout gameFrame; // Déclaration de FrameLayout
    private RecyclerView recyclerView;
    private BoardAdapter boardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hideNavigationBar();
        }
        setContentView(R.layout.activity_main);

        gameFrame = findViewById(R.id.gameFrame);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Créer une liste de cellules de plateau en forme de serpent
        List<BoardCell> boardCells = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            boardCells.add(new BoardCell("Cellule " + i));
        }

        // Adapter
        boardAdapter = new BoardAdapter(boardCells);
        recyclerView.setAdapter(boardAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }
}
