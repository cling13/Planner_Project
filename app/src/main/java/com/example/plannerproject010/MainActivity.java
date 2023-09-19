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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,ItemClickListner {

    MyGoogleMap mainGoogleMap;
    SimpleAdapter totalPlanAdapter;
    ItemTouchHelper helper;
    ArrayList<listClass> totalPlanList = new ArrayList<>(); //메인액티비티 플랜 저장하는 리스트
    SimpleAdapter msgBoxAdapter;

    public static final String MY_SECRET_KEY="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";
    public static final MediaType JSON=MediaType.get("application/json; charset=utf-8");
    //OkHttpClient client=new OkHttpClient();
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
        helper = new ItemTouchHelper(new ItemTouchHelperCallback(totalPlanAdapter));
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
        mainGoogleMap= new MyGoogleMap(googleMap);
        mainGoogleMap.setgMap();
    }

    @Override
    public void onItemBtnClick(int position) {

        String msg=totalPlanList.get(position).getName()+" 주변의 여행지 3곳만 알려줘 추가 설명 없이 이름만 말해줘";


        AlertDialog.Builder dlg=new AlertDialog.Builder(MainActivity.this);
        View search_msg_box=(View) getLayoutInflater().inflate(R.layout.search_msg_box,null);
        ArrayList<listClass> msgBoxList=new ArrayList<>();
        msgBoxAdapter = new SimpleAdapter(msgBoxList, this);
        RecyclerView msgBoxListView=(RecyclerView) search_msg_box.findViewById(R.id.msgBoxList);
        msgBoxListView.setLayoutManager(new LinearLayoutManager(this));
        msgBoxListView.setAdapter(msgBoxAdapter);
        helper.attachToRecyclerView(msgBoxListView);

        //gpt에서 추천 여행지 받아오기
        callAPI(msg, new GptCallback() {
            @Override
            public void onResponse(String gptResponse) {
                JSONObject jsonObject = null;
                try {
                    //아래 body().toString()이 아니라 .string() 주의
                    jsonObject = new JSONObject(gptResponse);
                    JSONArray jsonArray = jsonObject.getJSONArray("choices");
                    String gptReturnText = jsonArray.getJSONObject(0).getString("text");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int[] idx=new int[3];
                            String[] resText=new String[3];
                            idx[0]=gptReturnText.indexOf("1.");
                            idx[1]=gptReturnText.indexOf("2.");
                            idx[2]=gptReturnText.indexOf("3.");
                            resText[0]=gptReturnText.substring(idx[0]+3,idx[1]).replace(System.getProperty("line.separator"),"");
                            resText[1]=gptReturnText.substring(idx[1]+3,idx[2]).replace(System.getProperty("line.separator"),"");
                            resText[2]=gptReturnText.substring(idx[2]+3).replace(System.getProperty("line.separator"),"");

                            dlg.setView(search_msg_box);
                            msgBoxList.clear();
                            for(String msg:resText)
                                ((SearchActivity)SearchActivity.context).searchLocation(1,msg,msgBoxList,msgBoxAdapter);
                            dlg.show();
                        }
                    });
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(),"오류발생",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void callAPI(String question, GptCallback callback)
    {
        JSONObject object=new JSONObject();
        try{
            object.put("model","text-davinci-003");
            object.put("prompt", question);
            object.put("max_tokens", 4000);
            object.put("temperature", 0);
        }catch (JSONException e){
            e.printStackTrace();
        }
        RequestBody body=RequestBody.create(object.toString(),JSON);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // 연결 시간 초과 설정 (예: 30초)
                .readTimeout(30, TimeUnit.SECONDS)    // 읽기 시간 초과 설정 (예: 30초)
                .writeTimeout(30, TimeUnit.SECONDS)   // 쓰기 시간 초과 설정 (예: 30초)
                .build();

        Request request=new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization","Bearer "+MY_SECRET_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //실패시 콜백 호출
                callback.onFailure();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if(response.isSuccessful()){
                    // 성공 시 콜백 함수 호출
                    String responseBody = response.body().string();
                    callback.onResponse(responseBody);
                }
            }
        });
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
            mainGoogleMap.addMark(tmp.getlatLng(),tmp.getName());
        }
    });

    @Override
    public void onItemClick(int position) {
        LatLng latLng=totalPlanList.get(position).getlatLng();
    }
}