package com.example.plannerproject010;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> list=new ArrayList<>();
        for(int i=0; i<10; i++)
        {
            list.add(String.format("Text %d",i));
        }

        RecyclerView recyclerView=findViewById(R.id.planList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SimpleAdapter adapter=new SimpleAdapter(list);
        recyclerView.setAdapter(adapter);
    }
}

