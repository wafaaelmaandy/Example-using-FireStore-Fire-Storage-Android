package com.wafaaelm3andy.firestore.Model;

public class item {
    String text ;
    String imgurl ;
    public item(){}
    public item(String text, String photo_url) {
        this.text = text;
        this.imgurl = photo_url;
    }
    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getImgurl() {
        return imgurl;
    }



    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
