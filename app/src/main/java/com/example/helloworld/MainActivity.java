package com.example.helloworld;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BoardAdapter boardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
