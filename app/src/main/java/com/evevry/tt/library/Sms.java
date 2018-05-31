package com.evevry.tt.library;

import com.every.tt.model.ChatRoom;
import com.every.tt.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class Sms {
    public static final int SMS_NOTI_ID = 39;

    public static final int INPUT_STATE_IN = 1;
    public static final int INPUT_STATE_OUT = 0;

    public static final int READ_STATE_READ = 1;
    public static final int READ_STATE_UNREAD = 0;

    public static final int SEND_STATE_SUCCESS = 1;
    public static final int SEND_STATE_UNKNOWN = 0;
    public static final int SEND_STATE_FAIL = 2;
    public static final int SEND_STATE_NOT_SHOW = 3;

    private long id;
    private String phoneNumber;
    private String body;
    private long time;
    private int readState;
    private String imei;
    private int sendState = 0;
    private int inputState = 0;
    private String name;
    private int count;
    private long fid = 0;
    private String uriPhoto;

    public Sms() {
    }

    public Sms(com.every.tt.model.Sms sms) {
        this.id = sms.getId();
        this.phoneNumber = sms.getPhoneNumber();
        this.body = sms.getBody();
        this.time = sms.getTime();
        this.readState = sms.getReadState();
        this.imei = sms.getImei();
        this.inputState = sms.getInputState();
        this.sendState = sms.getSendState();
        this.name = sms.getName();
        this.count = sms.getCount();
        this.fid = sms.getFid();
        this.uriPhoto = sms.getUriPhoto();
    }

    public Sms(ChatRoom room) {
        this.phoneNumber = room.getPhoneNumber();
        this.body = room.getBody();
        this.time = room.getTime();
        this.readState = room.getReadState();
        this.imei = room.getImei();
        this.inputState = room.getInputState();
        this.sendState = room.getSendState();
        this.name = room.getName();
        this.count = room.getCount();
        this.fid = room.getFid();
        this.uriPhoto = room.getUriPhoto();
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setReadState(int readState) {
        this.readState = readState;
    }

    public int getReadState() {
        return readState;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImei() {
        return imei;
    }

    public void setInputState(int inputState) {
        this.inputState = inputState;
    }

    public int getInputState() {
        return inputState;
    }

    public void setSendState(int sendState) {
        this.sendState = sendState;
    }

    public int getSendState() {
        return sendState;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setFid(long fid) {
        this.fid = fid;
    }

    public long getFid() {
        return fid;
    }

    public void setUriPhoto(String uriPhoto) {
        this.uriPhoto = uriPhoto;
    }

    public String getUriPhoto() {
        return uriPhoto;
    }

    public boolean dateCompareTo(com.every.tt.model.Sms other, String dateFormat) {
        if (other != null) {

            String before = Util.convertToLocal(getTime(), dateFormat);
            String otherTime = Util.convertToLocal(other.getTime(), dateFormat);

            if (before.equals(otherTime))
                return true;
        }

        return false;
    }

    public String toJson() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("phoneNumber", phoneNumber);
            object.put("body", body);
            object.put("time", time);
            object.put("readState", readState);
            object.put("sendState", sendState);
            object.put("inputState", inputState);
            object.put("name", name);
            object.put("count", count);
            object.put("fid", fid);
            object.put("uriPhoto", uriPhoto);
            object.put("imei", imei);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }
}
