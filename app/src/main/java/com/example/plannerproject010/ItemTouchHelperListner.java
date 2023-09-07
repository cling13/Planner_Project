package com.example.plannerproject010;

import android.content.ClipData;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchHelperListner {
    boolean onItemMove(int from_position, int to_position);

    void onItemSwipe(int position);
}

