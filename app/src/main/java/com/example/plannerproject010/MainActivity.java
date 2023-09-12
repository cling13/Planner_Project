package com.example.plannerproject010;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,ItemClickListner {

    GoogleMap gMap;

    ArrayList<listClass> totalPlanList = new ArrayList<>(); //메인액티비티 플랜 저장하는 리스트
    SimpleAdapter totalPlanAdapter; //totalPlanListView 관리해주는 어댑터
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //맵 연결
        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mainMapFragment);
        mapFragment.getMapAsync(this);

        //xml 아이디 연결
        Button goSecActBtn = (Button) findViewById(R.id.button);
        RecyclerView totalPlanListView = findViewById(R.id.planList);

        //리사이클러뷰 어댑터와 핸들러 연결
        totalPlanListView.setLayoutManager(new LinearLayoutManager(this));
        totalPlanAdapter = new SimpleAdapter(totalPlanList, this);
        totalPlanListView.setAdapter(totalPlanAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelperCallback(totalPlanAdapter));
        helper.attachToRecyclerView(totalPlanListView);

        //세컨드 액티비티로 전환하는 버튼
        goSecActBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                mStartForResult.launch(intent);
            }
        });
    }

    //맵 초기설정
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap =googleMap;

        LatLng defaultLocation = new LatLng(37.541, 126.986);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation,12));

        Marker marker= gMap.addMarker(
                new MarkerOptions()
                   //     .icon();
        );
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onItemBtnClick(int position) {

    }

    //세컨드 액티비티에서 플랜 정보 받아와서 리스트에 추가해 주는 부분
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

        // getResultCode가 0일 경우 세컨드 액티비티에서 넘어옴
        if(result.getResultCode() == 1) {

            listClass tmp= (listClass) result.getData().getSerializableExtra("data");
            totalPlanList.add(tmp);
            totalPlanAdapter.notifyDataSetChanged();

            gMap.addMarker(new MarkerOptions().position(tmp.getlatLng()).title(tmp.getName()));
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tmp.getlatLng(), 15)); // 마커로 카메라 이동
        }
    });

}

