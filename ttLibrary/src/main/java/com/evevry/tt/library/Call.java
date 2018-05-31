package com.evevry.tt.library;

import org.json.JSONException;
import org.json.JSONObject;

public class Call {
    public static final int STATE_OUTGOING = 1;
    public static final int STATE_INCOMING = 2;
    public static final int STATE_MISS = 3;

    private long id;
    private String name;
    private String number;
    private long time;
    private long duration;
    private int state;
    private long pid;
    private String uriPhoto;

    public Call() {
    }

    public Call(com.every.tt.model.Call call) {
        if (call != null) {
            setId(call.getId());
            setName(call.getName());
            setNumber(call.getNumber());
            setTime(call.getTime());
            setState(call.getState());
            setPid(call.getPid());
            setDuration(call.getDuration());
        }
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public long getPid() {
        return pid;
    }

    public void setUriPhoto(String uriPhoto) {
        this.uriPhoto = uriPhoto;
    }

    public String getUriPhoto() {
        return uriPhoto;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String toJson() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("name", name);
            object.put("number", number);
            object.put("time", time);
            object.put("duration", duration);
            object.put("state", state);
            object.put("pid", pid);
            object.put("uriPhoto", uriPhoto);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
