package com.huso.yolarkadasim;

import java.io.Serializable;

public class yolculukilanlaridizisi implements Serializable {
    //is ilanindan gonderilen degerleri bir dize seklinde tutuyor

    private String yolculukid;
    private String yolculukadi;
    private String varisyeri;
    private String yolculukilaniniverenkullanici;
    private String yolculuktarihi;

    public yolculukilanlaridizisi(String yolculukid, String yolculukadi, String varisyeri, String yolculukilaniniverenkullanici, String yolculuktarihi) {
        this.yolculukid = yolculukid;
        this.yolculukadi = yolculukadi;
        this.varisyeri = varisyeri;
        this.yolculukilaniniverenkullanici = yolculukilaniniverenkullanici;
        this.yolculuktarihi = yolculuktarihi;
    }

    public String getYolculukid() {
        return yolculukid;
    }

    public void setYolculukid(String yolculukid) {
        this.yolculukid = yolculukid;
    }

    public String getYolculukadi() {
        return yolculukadi;
    }

    public void setYolculukadi(String yolculukadi) {
        this.yolculukadi = yolculukadi;
    }

    public String getvarisIsinadi() {
        return varisyeri;
    }

    public void setvarisIsinadi(String isinadi) {
        this.yolculukadi = varisyeri;
    }

    public String getYolculukilaniniverenkullanici(){
        return yolculukilaniniverenkullanici;
    }
    public void setYolculukilaniniverenkullanici(String yolculukilaniniverenkullanici){
        this.yolculukilaniniverenkullanici = yolculukilaniniverenkullanici;
    }

    public  String getYolculuktarihi(){
        return yolculuktarihi;
    }
    public void setYolculuktarihi(String yolculuktarihi){
        this.yolculuktarihi = yolculuktarihi;
    }
}
