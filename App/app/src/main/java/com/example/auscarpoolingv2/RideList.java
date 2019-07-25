package com.example.auscarpoolingv2;

class RideList {
    String name;
    String phoneNum;
    String date;
    String time;
    String address;

    public String getName() {
        return name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getAddress() {
        return address;
    }

    public RideList(String name, String phoneNum, String date, String time, String address) {
        this.name = name;
        this.phoneNum = phoneNum;
        this.date = date;
        this.time = time;
        this.address = address;
    }

    public String getAllDetails() {
        return "Name: " + name + "\nPhone number: " + phoneNum + "\nDate of ride: " + date + "\nTime: " + time + "\nLocation of pickup: " + address;
    }

}
