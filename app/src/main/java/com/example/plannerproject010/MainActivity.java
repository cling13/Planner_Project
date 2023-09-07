package com.example.plannerproject010;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

        Button btn=(Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg=new AlertDialog.Builder(MainActivity.this);
                View v=(View) View.inflate(MainActivity.this,R.layout.plantextbox,null);
                dlg.setView(v);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText et=(EditText) v.findViewById(R.id.placetext);
                        list.add(et.getText().toString());
                        adapter.notifyDataSetChanged();
                    }
                });
                dlg.show();
            }
        });
    }
}

