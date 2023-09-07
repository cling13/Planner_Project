package com.example.plannerproject010;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ArrayList<String> searchList=new ArrayList<>();

        RecyclerView recyclerSearchView=findViewById(R.id.searchList);
        recyclerSearchView.setLayoutManager(new LinearLayoutManager(this));

        SimpleAdapter searchadapter=new SimpleAdapter(searchList);
        recyclerSearchView.setAdapter(searchadapter);

        ItemTouchHelper helper=new ItemTouchHelper(new ItemTouchHelperCallback(searchadapter));
        helper.attachToRecyclerView(recyclerSearchView);


    }
}