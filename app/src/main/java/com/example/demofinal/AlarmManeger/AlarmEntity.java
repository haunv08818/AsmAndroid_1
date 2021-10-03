package com.example.demofinal.AlarmManeger;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AlarmEntity implements Serializable {

    public AlarmEntity(){

    }
    public AlarmEntity(long id, String message, String time, long longTime) {
        this.id = id;
        this.message = message;
        this.time = time;
        this.longTime = longTime;
    }

    @SerializedName("id")
    private long id;
    @SerializedName("message")
    private String message;
    @SerializedName("time")
    private String time;
    @SerializedName("longTime")
    private long longTime;

    public long getId() {
        return id++;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getLongTime() {
        return longTime;
    }

    public void setLongTime(long longTime) {
        this.longTime = longTime;
    }
}
