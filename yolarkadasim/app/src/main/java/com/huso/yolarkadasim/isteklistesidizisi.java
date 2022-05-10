package com.huso.yolarkadasim;

import java.io.Serializable;

public class isteklistesidizisi implements Serializable {
    private String isteklistesiid;
    private String isteklistesiadvesoyad;
    private String basvurankisi;
    private String basvurulanyolculukilaniid;
    private String isteklistesisehir;
    private String Tarih;
    private String yolculukadi;
    private String Varisadi;

    //isteklistesindeki degerleri alarak isteklistesinde diziye aktarma islemleri gerceklestirilir
    public isteklistesidizisi(String isteklistesiid, String isteklistesiadvesoyad, String basvurankisi, String basvurulanyolculukilaniid, String isteklistesisehir, String Tarih, String yolculukadi, String Varisadi) {
        this.isteklistesiid = isteklistesiid;
        this.isteklistesiadvesoyad = isteklistesiadvesoyad;
        this.basvurankisi = basvurankisi;
        this.basvurulanyolculukilaniid = basvurulanyolculukilaniid;
        this.isteklistesisehir=isteklistesisehir;
        this.Tarih=Tarih;
        this.yolculukadi = yolculukadi;
        this.Varisadi=Varisadi;
    }

    public String getIsteklistesiid() {
        return isteklistesiid;
    }

    public void setIsteklistesiid(String isteklistesiid) {
        this.isteklistesiid = isteklistesiid;
    }

    public String getIsteklistesiadvesoyad() {
        return isteklistesiadvesoyad;
    }

    public void setIsteklistesiadvesoyad(String isteklistesiadvesoyad) {
        this.isteklistesiadvesoyad = isteklistesiadvesoyad;
    }

    public String getBasvurankisi() {
        return basvurankisi;
    }

    public void setBasvurankisi(String basvurankisi) {
        this.basvurankisi = basvurankisi;
    }

    public String getBasvurulanyolculukilaniid() {
        return basvurulanyolculukilaniid;
    }

    public void setBasvurulanyolculukilaniid(String basvurulanyolculukilaniid) {
        this.basvurulanyolculukilaniid = basvurulanyolculukilaniid;
    }

    public String getIsteklistesisehir(){
        return isteklistesisehir;
    }
    public void setIsteklistesisehir(String isteklistesisehir){
        this.isteklistesisehir=isteklistesisehir;
    }

    public String getTarih(){
        return Tarih;
    }
    public void setTarih(String Tarih){
        this.Tarih=Tarih;
    }

    public String getYolculukadi(){
        return yolculukadi;
    }
    public void setYolculukadi(String yolculukadi){
        this.yolculukadi = yolculukadi;
    }
    public String getVarisadi(){
        return Varisadi;
    }
    public void setVarisadi(String Varisadi){
        this.Varisadi=Varisadi;
    }
}