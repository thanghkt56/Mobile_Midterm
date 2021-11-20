package com.example.loginauthfirebase;

public class Model {
    private String imageUrl;
    private String ISO;
    private String f;
    private String MM;
    private String Speed;
    private String Date;

    public Model(){

    }
    public Model(String imageUrl,String ISO,String f,String MM, String Speed,String Date) {
        this.imageUrl=imageUrl;
        this.ISO=ISO;
        this.f=f;
        this.MM=MM;
        this.Speed=Speed;
        this.Date=Date;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getISO() {
        return ISO;
    }

    public void setISO(String ISO) {
        this.ISO = ISO;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getMM() {
        return MM;
    }

    public void setMM(String MM) {
        this.MM = MM;
    }

    public String getSpeed() {
        return Speed;
    }

    public void setSpeed(String speed) {
        Speed = speed;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
