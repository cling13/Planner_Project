package com.example.plannerproject010;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
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
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    Button searchBtn;
    EditText sText;

    ArrayList<listClass> searchList;
    SimpleAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchList=new ArrayList<>();

        RecyclerView recyclerSearchView=findViewById(R.id.searchList);
        recyclerSearchView.setLayoutManager(new LinearLayoutManager(this));

        searchAdapter=new SimpleAdapter(searchList);
        recyclerSearchView.setAdapter(searchAdapter);

        ItemTouchHelper helper=new ItemTouchHelper(new ItemTouchHelperCallback(searchAdapter));
        helper.attachToRecyclerView(recyclerSearchView);

        searchBtn=(Button) findViewById(R.id.SearchBtn);
        sText=(EditText) findViewById(R.id.placeSearch);

        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchLocation();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap=googleMap;

        LatLng defaultLocation = new LatLng(37.541, 126.986);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation,12));


    }

    private void searchLocation() {
        String locationName = sText.getText().toString();

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
                List<AutocompletePrediction> predictionList=new ArrayList<>();

                if(task.isSuccessful()){
                    FindAutocompletePredictionsResponse response = task.getResult();
                    if(response!=null){
                        for(AutocompletePrediction prediction : response.getAutocompletePredictions()){
                            predictionList.add(prediction);

                            String placeId = prediction.getPlaceId();
                            String placeAddress = prediction.getFullText(null).toString();
                            String placeName =prediction.getPrimaryText(null).toString();

                            listClass tmp=new listClass();
                            tmp.setAddress(placeAddress);
                            tmp.setName(placeName);

                            searchAdapter.notifyDataSetChanged();


                            placesClient.fetchPlace(FetchPlaceRequest.builder(placeId, Arrays.asList(Place.Field.LAT_LNG)).build()).addOnCompleteListener(new OnCompleteListener<FetchPlaceResponse>() {
                                @Override
                                public void onComplete(@NonNull Task<FetchPlaceResponse> task) {
                                    if(task.isSuccessful()){
                                        FetchPlaceResponse fetchPlaceResponse=task.getResult();
                                        Place place=fetchPlaceResponse.getPlace();
                                        LatLng locationLatLng = place.getLatLng();
                                        List<PhotoMetadata> photoMetadataList = place.getPhotoMetadatas();

                                        if (photoMetadataList != null && !photoMetadataList.isEmpty()) {

                                            Toast.makeText(getApplicationContext(),"이미지 시작",Toast.LENGTH_SHORT).show();
                                            // 첫 번째 사진 메타데이터 가져오기
                                            PhotoMetadata photoMetadata = photoMetadataList.get(0);

                                            // 사진의 레퍼런스 가져오기
                                            String photoReference = photoMetadata.getAttributions();

                                            ImageView imageView=(ImageView) findViewById(R.id.imageView);
                                            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoReference + "&key=AIzaSyABN87oljSBD55FAbT9AgnYEGcDBFXuVCg";

                                            Picasso.get().load(photoUrl).into(imageView);

                                            Toast.makeText(getApplicationContext(),"이미지 완료",Toast.LENGTH_SHORT).show();
                                            // 사진을 다운로드하고 표시 또는 저장하는 작업 수행
                                            // 이 부분은 이미지 로딩 및 표시 또는 저장에 관련된 코드로 작성해야 합니다.
                                        }else {
                                            Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
                                        }

                                        //검색 결과 지도에 표시
                                        mMap.clear(); // 기존 마커 지우기
                                        mMap.addMarker(new MarkerOptions().position(locationLatLng).title(placeName));
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15)); // 마커로 카메라 이동
                                    } else {
                                        Toast.makeText(getApplicationContext(), "장소를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            break;
                        }
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "장소 검색에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}