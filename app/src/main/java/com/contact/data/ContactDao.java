package com.contact.data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDao {

    @Query("SELECT * FROM Contact")
    LiveData<List<Contact>> getAll();

    @Query("SELECT * FROM Contact WHERE cid = :id")
    LiveData<Contact> findById(int id);

    @Insert
    void insertAll(Contact... users);

    @Update
    void updateAll(Contact... users);

    @Delete
    void delete(Contact user);

}
