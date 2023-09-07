package com.example.plannerproject010;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

    private ArrayList<String> data = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView place;

        ViewHolder(View itemView) {
            super(itemView);

            place = itemView.findViewById(R.id.place);
        }
    }

    SimpleAdapter(ArrayList<String>list){
        data=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view=inflater.inflate(R.layout.itemlayout,parent,false);
        SimpleAdapter.ViewHolder vh=new SimpleAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {

        return 0;
    }
}
