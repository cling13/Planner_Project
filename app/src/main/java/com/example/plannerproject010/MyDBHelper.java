package com.example.plannerproject010;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDBHelper extends SQLiteOpenHelper {


    public MyDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE plantable (date CHAR(10) , id CHAR(50));");
        db.execSQL("CREATE TABLE movetable (date CHAR(10) , lat DOUBLE, lng DOUBLE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS plantable;");
            db.execSQL("DROP TABLE IF EXISTS movetable;");
        onCreate(db);
    }
}
