package com.example.plannerproject010;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> implements ItemTouchHelperListner {

    private ArrayList<String> data = null;

    @Override
    public boolean onItemMove(int from_position, int to_position) {
        String tmp = data.get(from_position);
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
        View view;
        Context context=parent.getContext();
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.itemlayout, parent, false);
        SimpleAdapter.ViewHolder vh=new SimpleAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String text = data.get(position);
        holder.place.setText(text);
    }

    @Override
    public int getItemCount() {

        return data.size();
    }
}
