package com.contact;

import android.app.Application;

import androidx.room.Room;

import com.contact.data.AppDatabase;

public class ContactApp extends Application {

    private static ContactApp  instance;
    private AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        db = Room.databaseBuilder(this,
                AppDatabase.class, "contact_db").build();
    }

    public static ContactApp getInstance() {
        return instance;
    }

    public AppDatabase getDataBase() {
        return db;
    }
}
