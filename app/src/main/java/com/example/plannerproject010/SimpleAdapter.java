package com.example.plannerproject010;

import static com.example.plannerproject010.MainActivity.context;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> implements ItemTouchHelperListner {

    private ArrayList<listClass> data = null;
    private  ItemClickListner itemClickListner;

    @Override
    public boolean onItemMove(int from_position, int to_position) {
        listClass tmp = data.get(from_position);
        data.remove(from_position);
        data.add(to_position, tmp);

        notifyItemMoved(from_position, to_position);
        return true;
    }

    @Override
    public void onItemSwipe(int position) {
        data.remove(position);
        MyDBHelper dbHelper=new MyDBHelper(context,"plantable",null,1);
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        String sql="DELETE FROM plantable WHERE id = '"+data.get(position).getId()+"';";
        db.execSQL(sql);
        db.close();
        notifyItemRemoved(position);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView addressText;
        ImageView placeImage;
        Button placeSelectBtn;

        ViewHolder(View itemView) {
            super(itemView);

            addressText=itemView.findViewById(R.id.placeAddress);
            nameText = itemView.findViewById(R.id.placeName);
            placeImage=itemView.findViewById(R.id.placeImage);
            placeSelectBtn=itemView.findViewById(R.id.placeSelectBtn);

            placeSelectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(itemClickListner!=null)
                    {
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                        {
                            itemClickListner.onItemBtnClick(position);
                        }
                    }
                }
            });
        }
    }

    SimpleAdapter(ArrayList<listClass>list, ItemClickListner itemClickListner){

        data=list;
        this.itemClickListner=itemClickListner;
        Places.initialize(context,"AIzaSyABN87oljSBD55FAbT9AgnYEGcDBFXuVCg");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Context context=parent.getContext();
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.itemlayout, parent, false);
        SimpleAdapter.ViewHolder vh=new SimpleAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {

        listClass text = data.get(position);
        holder.placeImage.setImageBitmap(text.getImage());
        holder.nameText.setText(text.getName());
        holder.addressText.setText(text.getAddress());
        holder.placeSelectBtn.setText(text.getBtnName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position=holder.getAdapterPosition();

                if(itemClickListner!=null){
                    itemClickListner.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {

        return data.size();
    }


    public void addItemList(String tmp,Context context)
    {
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.PHOTO_METADATAS // 사진 메타데이터 필드
        );

        FetchPlaceRequest request = FetchPlaceRequest.builder(tmp, placeFields).build();
        PlacesClient placesClient = Places.createClient(context);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            // 장소 정보 사용
            LatLng placeLatLng = place.getLatLng();
            String placeName = place.getName();
            String placeAddress = place.getAddress();

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
                    listClass t = new listClass(bitmap, placeName, placeAddress, placeLatLng,"추가",tmp);
                    ((MainActivity) MainActivity.context).addList(t);
                }).addOnFailureListener((exception) -> {
                });
            }

            // 사용 가능한 다른 정보도 여기에서 추출할 수 있습니다.
        }).addOnFailureListener((exception) -> {
            // 실패 처리
            exception.printStackTrace();
        });
    }
}

