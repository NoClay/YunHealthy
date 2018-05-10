package indi.noclay.cloudhealth.database;

import com.alibaba.fastjson.JSON;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobPointer;
import indi.noclay.cloudhealth.carddata.FoodShowItem;

/**
 * Created by NoClay on 2018/5/11.
 *
 * @Author NoClay
 * @Date 2018/5/11
 */

public class CollectionBean extends BmobObject{
    private BmobPointer owner;
    private String url; //加载详情页的url，唯一
    private String tag;
    private String value;

    public BmobPointer getOwner() {
        return owner;
    }

    public void setOwner(BmobPointer owner) {
        this.owner = owner;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(@TAG String tag){
        this.tag = tag;
    }


    public Object getValue(){
        switch (tag){
            case TAG.FOOD:{
                return JSON.parseObject(value, FoodShowItem.class);
            }
            case TAG.NEWS:{
                return JSON.parseObject(value, NewsData.class);
            }
        }
        return null;
    }

    public void setValue(Object value) {
        switch (value.getClass().getSimpleName()){
            case "FoodShowItem":{
                setTag(TAG.FOOD);
                break;
            }
            case "NewsData":{
                setTag(TAG.NEWS);
                break;
            }
        }
        this.value = JSON.toJSONString(value);
    }

    public @interface TAG{
        String FOOD = "food collection";
        String NEWS = "news collection";
    }
}
