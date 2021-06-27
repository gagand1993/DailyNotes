package com.dailynotes.util.model;

import androidx.annotation.Nullable;

import com.dailynotes.util.helper.AppController;
import com.dailynotes.util.helper.ConstantUtility;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;

public   class  Notes implements Serializable {
    String id;
    public Notes(){

    }
    public Notes(String id, String date, String data) {
        this.id = id;
        this.date = date;
        this.data = data;


    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    String data;

}