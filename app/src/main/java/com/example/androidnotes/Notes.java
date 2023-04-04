package com.example.androidnotes;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.jar.JarException;

import androidx.annotation.NonNull;

public class Notes implements Comparable<Notes>, Serializable{
    private String title,des;
    private long date;

    public Notes(String title, String des, long savedate){
        this.title=title;
        this.des=des;
        this.date=savedate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        return "Notes{" +
                "title='" + title + '\'' +
                "des='" + des + '\'' +
                "date"+ date + '}';
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject obj = new JSONObject();

        obj.put("title", title);
        obj.put("des", des);
        obj.put("date", date);
        return obj;
    }

    public static Notes createFromJSON(JSONObject jsonObject) throws JSONException {
        String title = jsonObject.getString("title");
        String des = jsonObject.getString("des");
        long date = jsonObject.getLong("date");
        return new Notes(title, des, date);
    }

    @Override
    public int compareTo(Notes note) {

        //return (int) (date - note.date);
        long diff = date - note.date;
        if (diff > 1){
            return -1;
        } else if(diff < 0){
            return 1;
        }
        else return 0;
    }

}


