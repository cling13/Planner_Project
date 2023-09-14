package com.example.plannerproject010;

import static com.android.volley.VolleyLog.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    String gptReturnText;
    GoogleMap gMap;
    TextView textViewtest;

    ArrayList<listClass> totalPlanList = new ArrayList<>(); //메인액티비티 플랜 저장하는 리스트
    SimpleAdapter totalPlanAdapter; //totalPlanListView 관리해주는 어댑터

    public static final String MY_SECRET_KEY="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";
    public static final MediaType JSON=MediaType.get("application/json; charset=utf-8");
    //OkHttpClient client=new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         textViewtest=findViewById(R.id.textViewtest);

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

//        Marker marker= gMap.addMarker(
//                new MarkerOptions()
//                        .icon();
//        );
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onItemBtnClick(int position) {
        String msg=totalPlanList.get(position).getName();
        String addmsg=" 주변의 여행지 3곳만 알려줘 추가 설명 없이 이름만 말해줘";
        msg=msg+addmsg;

        //gpt에서 추천 여행지 받아오기
        callAPI(msg, new GptCallback() {
            @Override
            public void onResponse(String gptResponse) {
                JSONObject jsonObject = null;
                try {
                    //아래 body().toString()이 아니라 .string() 주의
                    jsonObject = new JSONObject(gptResponse);
                    JSONArray jsonArray = jsonObject.getJSONArray("choices");
                    gptReturnText = jsonArray.getJSONObject(0).getString("text");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewtest.setText(gptReturnText);
                            int fir=gptReturnText.indexOf("1.");
                            int sec=gptReturnText.indexOf("2.");
                            int thi=gptReturnText.indexOf("3.");
                            String tFir=gptReturnText.substring(fir+3,sec);
                            String tSec=gptReturnText.substring(sec+3,thi);
                            String tThi=gptReturnText.substring(thi+3);
                            tFir=tFir.replace(System.getProperty("line.separator"),"");
                            tSec=tSec.replace(System.getProperty("line.separator"),"");
                            tThi=tThi.replace(System.getProperty("line.separator"),"");

                            textViewtest.setText(tFir+" "+tSec+" "+tThi);
                        }
                    });
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure() {
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
                if (!response.isSuccessful()) {
                    Log.e(TAG, "서버 응답 실패: " + response.code() + " - " + response.message());
                }

                if(response.isSuccessful()){
                    int responseCode = response.code();
                    if (responseCode == 200) {
                        // 성공 시 콜백 함수 호출
                        String responseBody = response.body().string();
                        callback.onResponse(responseBody);
                    } else {
                        // 다른 응답 코드에 대한 처리
                        callback.onFailure();
                    }


                }
            }
        });
    }

    public interface GptCallback{
        void onResponse(String gptResponse);
        void onFailure();
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

