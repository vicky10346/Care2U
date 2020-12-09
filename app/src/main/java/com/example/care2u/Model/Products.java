package com.example.care2u.Model;

public class Products {

    private String pName, pImage, pPrice, pDesc, category, pId, date, time, pCode;

    public Products(){

    }

    public Products(String pName, String pImage, String pPrice, String pDesc, String category, String pId, String date, String time, String pCode) {
        this.pName = pName;
        this.pImage = pImage;
        this.pPrice = pPrice;
        this.pDesc = pDesc;
        this.category = category;
        this.pId = pId;
        this.date = date;
        this.time = time;
        this.pCode = pCode;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpPrice() {
        return pPrice;
    }

    public void setpPrice(String pPrice) {
        this.pPrice = pPrice;
    }

    public String getpDesc() {
        return pDesc;
    }

    public void setpDesc(String pDesc) {
        this.pDesc = pDesc;
    }

    public String getcategory() {
        return category;
    }

    public void setcategory(String pCategory) {
        this.category = pCategory;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getpCode() {
        return pCode;
    }

    public void setpCode(String pCode) {
        this.pCode = pCode;
    }
}
