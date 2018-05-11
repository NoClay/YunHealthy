package indi.noclay.cloudhealth.database;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import indi.noclay.cloudhealth.interfaces.CollectionFormat;

/**
 * Created by no_clay on 2017/2/7.
 */

public class NewsData extends BmobObject implements Serializable, CollectionFormat{
    private String date;
    private String content;
    private String url;
    private String title;
    private SignUserData owner;

    public String getName(){
        return title;
    }
    public SignUserData getOwner() {
        return owner;
    }

    public void setOwner(SignUserData owner) {
        this.owner = owner;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
