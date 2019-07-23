package com.example.auscarpoolingv2;

public class Users {

    private String name;
    private String phonenum;
    private String date;
    private String genderpref;
    private Double latitude;
    private Double logitude;
    private boolean providing;
    private Double rating;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGenderpref() {
        return genderpref;
    }

    public void setGenderpref(String genderpref) {
        this.genderpref = genderpref;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLogitude() {
        return logitude;
    }

    public void setLogitude(Double logitude) {
        this.logitude = logitude;
    }

    public boolean isProviding() {
        return providing;
    }

    public void setProviding(boolean providing) {
        this.providing = providing;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isToAUS() {
        return toAUS;
    }

    public void setToAUS(boolean toAUS) {
        this.toAUS = toAUS;
    }

    private String time;
    private boolean toAUS;


    public String getName() {
        return name;
    }


    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public void setName(String name) {
        this.name = name;
    }
}
