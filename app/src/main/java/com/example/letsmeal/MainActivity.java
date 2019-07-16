package com.example.letsmeal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true); // 뭔 소리지?
        recyclerView.setLayoutManager(linearLayoutManager);

        final RecyclerViewAdapter recyclerViewAdapter =
                new RecyclerViewAdapter(getApplicationContext(), R.layout.activity_main);

        final FloatingActionButton addItemFab = (FloatingActionButton) findViewById(R.id.addItemFab);
        addItemFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ItemCard newCard = new ItemCard((int)System.currentTimeMillis(), "그 돈으로 차라리 국밥을 사먹겠다");

                recyclerViewAdapter.addItemCard(newCard);
                recyclerViewAdapter.setContext(getApplicationContext());
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        });

    }


}
