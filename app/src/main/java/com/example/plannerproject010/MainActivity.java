package com.example.plannerproject010;

import static com.android.volley.VolleyLog.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, ItemClickListner {

    MyGoogleMap mainGoogleMap;
    SimpleAdapter totalPlanAdapter;
    ArrayList<listClass> totalPlanList = new ArrayList<>(); //메인액티비티 플랜 저장하는 리스트
    ArrayList<listClass> msgBoxList;
    SQLiteDatabase sqlDB;
    MyDBHelper myDBHelper;

    public static Context context;
    DatePicker datePicker;

    ItemClickListner itemClickListner= new ItemClickListner() {
        @Override
        public void onItemClick(int position) {
        }

        @Override
        public void onItemBtnClick(int position) {
            addDB(msgBoxList.get(position));
            addList(msgBoxList.get(position));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;

        myDBHelper = new MyDBHelper(this,"plantable",null,1);
        //맵 연결
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mainMapFragment);
        mapFragment.getMapAsync(this);

        //xml 아이디 연결
        Button goSecActBtn = (Button) findViewById(R.id.button);
        RecyclerView totalPlanListView = findViewById(R.id.planList);

        //리사이클러뷰 어댑터와 핸들러 연결
        totalPlanListView.setLayoutManager(new LinearLayoutManager(this));
        totalPlanAdapter = new SimpleAdapter(totalPlanList, this);
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelperCallback(totalPlanAdapter));
        totalPlanListView.setAdapter(totalPlanAdapter);
        helper.attachToRecyclerView(totalPlanListView);
        datePicker = (DatePicker) findViewById(R.id.datePicker);

        //세컨드 액티비티로 전환하는 버튼
        goSecActBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                mStartForResult.launch(intent);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                    //읽어오기
                    String date=Integer.toString(i)+"-"+Integer.toString(i1)+"-"+Integer.toString(i2);
                    totalPlanList.clear();
                    sqlDB=myDBHelper.getReadableDatabase();
                    String sql="select * from plantable WHERE date = '"+date+"';";
                    Log.d("sql2",sql);
                    Cursor cursor = sqlDB.rawQuery(sql,null);
                    while(cursor.moveToNext()){
                        totalPlanAdapter.addItemList(cursor.getString(1),MainActivity.this);
                    }
                    totalPlanAdapter.notifyDataSetChanged();
                    sqlDB.close();
                }
            });
        }

        Button btn=(Button) findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDB=myDBHelper.getWritableDatabase();
                myDBHelper.onUpgrade(sqlDB,1,1);
                sqlDB.close();
            }
        });

    }

    //맵 초기설정
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mainGoogleMap = new MyGoogleMap(googleMap);
        mainGoogleMap.setgMap();
    }

    @Override
    public void onItemBtnClick(int position) {

        String msg = totalPlanList.get(position).getName() + " 주변의 여행지 3곳만 알려줘 추가 설명 없이 이름만 말해줘";

        View search_msg_box = (View) getLayoutInflater().inflate(R.layout.search_msg_box, null);
        msgBoxList = new ArrayList<>();
        SimpleAdapter msgBoxAdapter = new SimpleAdapter(msgBoxList, itemClickListner);
        RecyclerView msgBoxListView = (RecyclerView) search_msg_box.findViewById(R.id.msgBoxList);
        msgBoxListView.setLayoutManager(new LinearLayoutManager(this));
        msgBoxListView.setAdapter(msgBoxAdapter);

        //gpt에서 추천 여행지 받아오기
        GptCallback gptCallback = new GptCallback();
        CompletableFuture<String> apiResponseFuture = gptCallback.callAPI(msg);

        // CompletableFuture의 thenAcceptAsync 메서드를 사용하여 API 응답을 처리
        apiResponseFuture.thenAcceptAsync(response -> {
            // API 호출 성공 시 처리 (response 변수에 API 응답이 들어 있습니다)
            Log.d("API Response", response);

            runOnUiThread(() -> {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray choicesArray = responseObject.getJSONArray("choices");

                    // choices 배열의 첫 번째 객체 가져오기
                    JSONObject firstChoice = choicesArray.getJSONObject(0);

                    // 첫 번째 객체에서 "text" 속성 가져오기
                    String gptReturnText = firstChoice.getString("text");

                    int[] idx = new int[3];
                    String[] resText = new String[3];
                    idx[0] = gptReturnText.indexOf("1.");
                    idx[1] = gptReturnText.indexOf("2.");
                    idx[2] = gptReturnText.indexOf("3.");
                    resText[0] = gptReturnText.substring(idx[0] + 3, idx[1]).replace(System.getProperty("line.separator"), "");
                    resText[1] = gptReturnText.substring(idx[1] + 3, idx[2]).replace(System.getProperty("line.separator"), "");
                    resText[2] = gptReturnText.substring(idx[2] + 3).replace(System.getProperty("line.separator"), "");

                    AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                    dlg.setTitle("주변 추천 여행지");
                    dlg.setView(search_msg_box);
                    msgBoxList.clear();
                    for (String gptmsg : resText) {
                        ((SearchActivity) SearchActivity.context).searchLocation(1, gptmsg, msgBoxList, msgBoxAdapter);
                    }
                    dlg.setPositiveButton("닫기",null);
                    dlg.show();

                } catch (Exception e) {
                    Log.d("error", e.toString());
                }
                ;
            });
        }).exceptionally(e -> {
            // API 호출 실패 시 처리
            Log.e("API Error", "Error occurred: " + e.getMessage());
            return null;
        });
    }


    //세컨드 액티비티에서 플랜 정보 받아와서 리스트에 추가해 주는 부분
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                // getResultCode가 0일 경우 세컨드 액티비티에서 넘어옴
                if (result.getResultCode() == 1) {
                    listClass tmp = (listClass) result.getData().getSerializableExtra("data");
                    addDB(tmp);
                    addList(tmp);
                }
            });

    @Override
    public void onItemClick(int position) {
        addDB(msgBoxList.get(position));
        addList(msgBoxList.get(position));
    }

    void addList(listClass tmp)
    {

        tmp.setBtnName("주변 검색");
        totalPlanList.add(tmp);
        totalPlanAdapter.notifyDataSetChanged();
        mainGoogleMap.addMark(tmp.getlatLng(), tmp.getName());
    }
    void addDB(listClass tmp)
    {
        sqlDB=myDBHelper.getReadableDatabase();
        String date = Integer.toString(datePicker.getYear())+"-"+Integer.toString(datePicker.getMonth())+"-"+Integer.toString(datePicker.getDayOfMonth());
        String sql="INSERT INTO plantable VALUES ('"+date+"', '"+tmp.getId()+"');";
        Log.d("sql",sql);
        sqlDB.execSQL(sql);
    }
}

