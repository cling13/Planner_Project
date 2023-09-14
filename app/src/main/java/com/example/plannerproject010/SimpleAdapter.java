package com.example.plannerproject010;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
}

