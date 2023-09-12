package com.example.plannerproject010;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback,ItemClickListner {

    GoogleMap gMap;
    Button placeSearchBtn;
    EditText placeSearchText;

    ArrayList<listClass> placeSearchList;
    SimpleAdapter placeSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //초기 리스트와 어댑터 선언및 설정
        placeSearchList =new ArrayList<>();
        placeSearchAdapter =new SimpleAdapter(placeSearchList,this);
        ItemTouchHelper helper=new ItemTouchHelper(new ItemTouchHelperCallback(placeSearchAdapter));

        RecyclerView recyclerSearchView=findViewById(R.id.placeSearchList);
        recyclerSearchView.setLayoutManager(new LinearLayoutManager(this));
        recyclerSearchView.setAdapter(placeSearchAdapter);
        helper.attachToRecyclerView(recyclerSearchView);

        placeSearchBtn =(Button) findViewById(R.id.placeSearchBtn);
        placeSearchText =(EditText) findViewById(R.id.placeSearchText);

        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        //검색버튼 클릭 이벤트
        placeSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeSearchList.clear();
                searchLocation();
            }
        });


    }

    //map초기 설정
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap =googleMap;

        LatLng defaultLocation = new LatLng(37.541, 126.986);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation,12));
    }

    private void searchLocation() {
        String locationName = placeSearchText.getText().toString();

        //place 검색 요청 생성
        Places.initialize(getApplicationContext(),"AIzaSyABN87oljSBD55FAbT9AgnYEGcDBFXuVCg");
        PlacesClient placesClient = Places.createClient(this);
        AutocompleteSessionToken token=AutocompleteSessionToken.newInstance();

        //place 검색 요청 설정
        FindAutocompletePredictionsRequest request= FindAutocompletePredictionsRequest.builder()
                .setQuery(locationName)
                .setSessionToken(token)
                .build();

        //place 검색 요청 실행
        placesClient.findAutocompletePredictions(request).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {

                if(task.isSuccessful()){
                    FindAutocompletePredictionsResponse response = task.getResult();
                    if(response!=null){
                        //검색정보 전체 리스트에 저장후 하나씩 반복
                        List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
                        for(AutocompletePrediction prediction : predictions){

                            String placeId = prediction.getPlaceId();

                            //place로 가져올 정보 선언
                            List<Place.Field> placeFields = Arrays.asList(
                                    Place.Field.ID,
                                    Place.Field.NAME,
                                    Place.Field.ADDRESS,
                                    Place.Field.LAT_LNG,
                                    Place.Field.PHOTO_METADATAS // 사진 메타데이터 필드
                            );

                            //한개의 place로 정보 받아온뒤 저장
                            placesClient.fetchPlace(FetchPlaceRequest.builder(placeId, placeFields).build()).addOnCompleteListener(new OnCompleteListener<FetchPlaceResponse>() {
                                @Override
                                public void onComplete(@NonNull Task<FetchPlaceResponse> task) {
                                    if(task.isSuccessful()){
                                        FetchPlaceResponse fetchPlaceResponse=task.getResult();
                                        Place place=fetchPlaceResponse.getPlace();


                                        LatLng placeLatLng = place.getLatLng();
                                        String placeName=place.getName();
                                        String placeAddress=place.getAddress();

                                        //사진 정보 가져오기
                                        List<PhotoMetadata> photoMetadataList = place.getPhotoMetadatas();

                                        if (photoMetadataList != null && !photoMetadataList.isEmpty()) {

                                            //사진 메타데이터 가져오기
                                            PhotoMetadata photoMetadata = photoMetadataList.get(0);

                                            //사진 크기 설정
                                            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                                    .setMaxWidth(500) // Optional.
                                                    .setMaxHeight(500) // Optional.
                                                    .build();

                                            //리스트에 여행지 정보 추가
                                            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                                                        Bitmap bitmap = fetchPhotoResponse.getBitmap();
                                                        listClass tmp=new listClass(bitmap,placeName,placeAddress,placeLatLng);
                                                        placeSearchList.add(tmp);
                                                        placeSearchAdapter.notifyDataSetChanged();

                                                    }).addOnFailureListener((exception)->{
                                            });
                                        }

                                        //검색 결과 지도에 표시

                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    //리스트에서 아이템 클릭시 해당 정보를 메인 액티비티로 전송해주는 부분
    @Override
    public void onItemClick(int position) {
        LatLng latLng=placeSearchList.get(position).getlatLng();
        gMap.clear(); // 기존 마커 지우기
        gMap.addMarker(new MarkerOptions().position(latLng).title(placeSearchList.get(position).getName()));
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15)); // 마커로 카메라 이동
    }

    @Override
    public void onItemBtnClick(int position) {
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        listClass listPosition = placeSearchList.get(position);

        listClass tmp=new listClass(listPosition.getImage(),listPosition.getName(),listPosition.getAddress(),listPosition.getlatLng());

        intent.putExtra("data",tmp);

        setResult(1,intent);
        finish();
    }
}