package com.example.plannerproject010;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ItemClickListner {

    ArrayList<listClass> list = new ArrayList<>();
    SimpleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.planList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SimpleAdapter(list, this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
        helper.attachToRecyclerView(recyclerView);

        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                mStartForResult.launch(intent);
            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            Toast.makeText(getApplicationContext(), "ㅎㅇ", Toast.LENGTH_SHORT).show();
            String name = data.getStringExtra("name");
            String address = data.getStringExtra("address");
            Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
            //Bitmap image = data.getParcelableExtra("image");
        }

    }

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

        // SubOne에서 결과를 받아옴
        if(result.getResultCode() == 0) {
            String name = result.getData().getStringExtra("name");
            String address = result.getData().getStringExtra("address");
            byte[] arr=result.getData().getByteArrayExtra("image");
            Bitmap bitmap= BitmapFactory.decodeByteArray(arr,0,arr.length);


            listClass tmp=new listClass(bitmap,name,address);
            list.add(tmp);
            adapter.notifyDataSetChanged();
        }
    });

}

