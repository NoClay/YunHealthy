package indi.noclay.cloudhealth.carddata;

import java.io.Serializable;
import java.util.List;

/**
 * Created by NoClay on 2018/5/3.
 */

public class IllnessInfoRetData extends ShowApiRetBase implements Serializable{
    @Override
    public List<Object> getDataItem() {
        return null;
    }

    public IllnessItem getItem() {
        return item;
    }

    public void setItem(IllnessItem item) {
        this.item = item;
    }

    private IllnessItem item;
    public static class IllnessItem implements Serializable{
        private String typeName;
        private String id;
        private String summary;
        private String subTypeName;
        private String name;
        private List<TagData> tagList;
        private String subTypeId;
        private String typeId;
        private String ct;

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getSubTypeName() {
            return subTypeName;
        }

        public void setSubTypeName(String subTypeName) {
            this.subTypeName = subTypeName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<TagData> getTagList() {
            return tagList;
        }

        public void setTagList(List<TagData> tagList) {
            this.tagList = tagList;
        }

        public String getSubTypeId() {
            return subTypeId;
        }

        public void setSubTypeId(String subTypeId) {
            this.subTypeId = subTypeId;
        }

        public String getTypeId() {
            return typeId;
        }

        public void setTypeId(String typeId) {
            this.typeId = typeId;
        }

        public String getCt() {
            return ct;
        }

        public void setCt(String ct) {
            this.ct = ct;
        }
    }
}
