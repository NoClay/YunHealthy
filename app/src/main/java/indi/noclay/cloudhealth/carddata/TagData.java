package indi.noclay.cloudhealth.carddata;

import java.io.Serializable;

/**
 * Created by NoClay on 2018/5/3.
 */

public class TagData implements Serializable{
    private String name;
    private String content;

    public TagData() {
    }

    public TagData(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
